package engine.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import engine.core.CurrentGameState;
import engine.model.Interactable;
import engine.model.Room;
import engine.model.Player;
import engine.model.Item;

import java.util.ArrayList;
import java.util.List;

public class UseCommand implements InterfaceCommand, ConfigurableCommand {

    private static final Gson GSON = new Gson();

    private UseConfig config = new UseConfig();

    @Override
    public void configure(JsonObject configJson) {
        if (configJson == null) {
            return;
        }

        UseConfig parsedConfig = GSON.fromJson(configJson, UseConfig.class);
        if (parsedConfig != null) {
            config = parsedConfig;
        }
    }

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Use what?");
            return;
        }

        UseRequest request = UseRequest.from(args);
        String target = request.getActionTarget();

        HealingItem healingItem = config.findHealingItem(request.getUsedItemOrTarget());
        if (healingItem != null) {
            useHealingItem(gameState, healingItem);
            return;
        }

        if (engine.systems.ThreatSystem.triggerEnemyAttack(gameState, "use " + target)) {
            return;
        }

        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        Interactable interactable = currentRoom.findInteractableByName(request.getInteractableTarget());

        if (interactable == null) {
            System.out.println("You can't use that here.");
            return;
        }

        Item usedItem = null;
        if (request.hasUsedItem()) {
            usedItem = findInventoryItem(gameState, request.getUsedItemOrTarget());

            if (usedItem == null) {
                System.out.println("You do not have that.");
                return;
            }
        }

        if (interactable.getForbiddenFlag() != null &&
                gameState.isFlagTrue(interactable.getForbiddenFlag())) {
            String message = interactable.getForbiddenMessage() != null
                    ? interactable.getForbiddenMessage()
                    : interactable.getFailureMessage();
            System.out.println(message);
            return;
        }

        if (interactable.getRequiredFlag() != null &&
                !gameState.isFlagTrue(interactable.getRequiredFlag())) {
            System.out.println(interactable.getFailureMessage());
            return;
        }

        if (usedItem != null
                && interactable.getRequiredItemId() != null
                && !usedItem.getId().equalsIgnoreCase(interactable.getRequiredItemId())) {
            System.out.println(interactable.getFailureMessage());
            return;
        }

        if (interactable.getRequiredItemId() != null &&
                gameState.getPlayer().findItemById(interactable.getRequiredItemId()) == null) {
            System.out.println(interactable.getFailureMessage());
            return;
        }

        if (interactable.getRequiredItemId() != null) {
            var item = gameState.getPlayer().findItemById(interactable.getRequiredItemId());
            if (item != null) {
                gameState.getPlayer().removeItem(item);
            }
        }

        if (interactable.getSetsFlag() != null) {
            gameState.setFlag(interactable.getSetsFlag(), true);
        }



        System.out.println(interactable.getSuccessMessage());


    }

    private Item findInventoryItem(CurrentGameState gameState, String itemName) {
        for (Item item : gameState.getPlayer().getInventory()) {
            if (matchesItem(itemName, item)) {
                return item;
            }
        }

        return null;
    }

    private boolean matchesItem(String itemName, Item item) {
        if (item == null || itemName == null) {
            return false;
        }

        String normalizedInput = itemName.toLowerCase().trim();
        return matchesValue(normalizedInput, item.getId())
                || matchesValue(normalizedInput, item.getName());
    }

    private boolean matchesValue(String normalizedInput, String value) {
        if (value == null) {
            return false;
        }

        String normalizedValue = value.toLowerCase().replace("_", " ");
        return normalizedInput.equals(normalizedValue);
    }

    private void useHealingItem(CurrentGameState gameState, HealingItem healingItem) {
        Player player = gameState.getPlayer();

        if (!player.isInjured()) {
            System.out.println(healingItem.getNotNeededMessage());
            return;
        }

        Item item = player.findItemById(healingItem.getItemId());

        if (item == null) {
            System.out.println(healingItem.getMissingItemMessage());
            return;
        }

        player.removeItem(item);
        player.heal();

        for (String message : healingItem.getSuccessMessages()) {
            System.out.println(message);
        }
    }

    private static class UseConfig {
        private List<HealingItem> healingItems;

        private HealingItem findHealingItem(String target) {
            for (HealingItem healingItem : getHealingItems()) {
                if (healingItem.matches(target)) {
                    return healingItem;
                }
            }

            return null;
        }

        private List<HealingItem> getHealingItems() {
            if (healingItems == null) {
                healingItems = new ArrayList<>();
            }

            return healingItems;
        }
    }

    private static class HealingItem {
        private String itemId;
        private List<String> aliases;
        private String notNeededMessage;
        private String missingItemMessage;
        private List<String> successMessages;

        private boolean matches(String target) {
            if (itemId != null) {
                String normalizedId = itemId.toLowerCase();
                if (target.equals(normalizedId) || target.equals(normalizedId.replace("_", " "))) {
                    return true;
                }
            }

            for (String alias : getAliases()) {
                if (alias.equalsIgnoreCase(target)) {
                    return true;
                }
            }

            return false;
        }

        private String getItemId() {
            return itemId;
        }

        private List<String> getAliases() {
            if (aliases == null) {
                aliases = new ArrayList<>();
            }

            return aliases;
        }

        private String getNotNeededMessage() {
            return notNeededMessage != null ? notNeededMessage : "You do not need that right now.";
        }

        private String getMissingItemMessage() {
            return missingItemMessage != null ? missingItemMessage : "You do not have that.";
        }

        private List<String> getSuccessMessages() {
            if (successMessages == null) {
                successMessages = new ArrayList<>();
            }

            return successMessages;
        }
    }

    private static class UseRequest {
        private final String usedItemOrTarget;
        private final String interactableTarget;
        private final String actionTarget;

        private UseRequest(String usedItemOrTarget, String interactableTarget, String actionTarget) {
            this.usedItemOrTarget = usedItemOrTarget;
            this.interactableTarget = interactableTarget;
            this.actionTarget = actionTarget;
        }

        private static UseRequest from(List<String> args) {
            int prepositionIndex = findPreposition(args);

            if (prepositionIndex > 0 && prepositionIndex < args.size() - 1) {
                String usedItem = String.join(" ", args.subList(0, prepositionIndex)).toLowerCase();
                String target = String.join(" ", args.subList(prepositionIndex + 1, args.size())).toLowerCase();
                return new UseRequest(usedItem, target, usedItem + " " + args.get(prepositionIndex) + " " + target);
            }

            String target = String.join(" ", args).toLowerCase();
            return new UseRequest(target, target, target);
        }

        private static int findPreposition(List<String> args) {
            for (int i = 0; i < args.size(); i++) {
                String token = args.get(i);
                if ("on".equalsIgnoreCase(token) || "with".equalsIgnoreCase(token)) {
                    return i;
                }
            }

            return -1;
        }

        private boolean hasUsedItem() {
            return !usedItemOrTarget.equals(interactableTarget);
        }

        private String getUsedItemOrTarget() {
            return usedItemOrTarget;
        }

        private String getInteractableTarget() {
            return interactableTarget;
        }

        private String getActionTarget() {
            return actionTarget;
        }
    }

}
