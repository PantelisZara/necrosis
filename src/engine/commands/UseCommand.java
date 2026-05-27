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

        String target = String.join(" ", args).toLowerCase();

        HealingItem healingItem = config.findHealingItem(target);
        if (healingItem != null) {
            useHealingItem(gameState, healingItem);
            return;
        }

        if (engine.systems.ThreatSystem.triggerEnemyAttack(gameState, "use " + target)) {
            return;
        }

        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        Interactable interactable = currentRoom.findInteractableByName(target);

        if (interactable == null) {
            System.out.println("You can't use that here.");
            return;
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

}
