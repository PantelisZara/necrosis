package engine.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import engine.core.CurrentGameState;
import engine.model.Exit;
import engine.model.Item;
import engine.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseCommand implements InterfaceCommand, ConfigurableCommand {

    private static final Gson GSON = new Gson();

    private ChooseConfig config = new ChooseConfig();

    @Override
    public void configure(JsonObject configJson) {
        if (configJson == null) {
            return;
        }

        ChooseConfig parsedConfig = GSON.fromJson(configJson, ChooseConfig.class);
        if (parsedConfig != null) {
            config = parsedConfig;
        }
    }

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println(config.getMissingChoiceMessage());
            return;
        }

        if (config.getAvailableFlag() != null && !gameState.isFlagTrue(config.getAvailableFlag())) {
            System.out.println(config.getUnavailableMessage());
            return;
        }

        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        if (config.getRequiredRoomId() != null
                && (currentRoom == null || !currentRoom.getId().equalsIgnoreCase(config.getRequiredRoomId()))) {
            System.out.println(config.getWrongRoomMessage());
            return;
        }

        String choiceInput = String.join(" ", args).toLowerCase().trim();
        ChoiceOption choice = findChoice(choiceInput);

        if (choice == null) {
            System.out.println(config.getInvalidChoiceMessage());
            return;
        }

        List<Item> itemsToAdd = createItems(gameState, choice);
        if (itemsToAdd == null) {
            return;
        }

        for (Map.Entry<String, Boolean> entry : choice.getSetFlags().entrySet()) {
            gameState.setFlag(entry.getKey(), entry.getValue());
        }

        for (Item item : itemsToAdd) {
            gameState.getPlayer().addItem(item);
        }

        for (ExitUnlock unlock : choice.getUnlockExits()) {
            Room room = gameState.getRoom(unlock.roomId);
            if (room == null) {
                continue;
            }

            Exit exit = room.getExit(unlock.direction);
            if (exit != null) {
                exit.unlock();
            }
        }

        for (String message : choice.getMessages()) {
            System.out.println(message);
        }
    }

    private ChoiceOption findChoice(String choiceInput) {
        for (ChoiceOption choice : config.getChoices()) {
            if (choice.matches(choiceInput)) {
                return choice;
            }
        }

        return null;
    }

    private List<Item> createItems(CurrentGameState gameState, ChoiceOption choice) {
        List<Item> items = new ArrayList<>();

        for (String itemId : choice.getAddItemIds()) {
            Item item = gameState.createItemById(itemId);
            if (item == null) {
                System.out.println(choice.getMissingItemMessage());
                return null;
            }

            items.add(item);
        }

        return items;
    }

    private static class ChooseConfig {
        private String availableFlag;
        private String requiredRoomId;
        private String missingChoiceMessage;
        private String unavailableMessage;
        private String wrongRoomMessage;
        private String invalidChoiceMessage;
        private List<ChoiceOption> choices;

        private String getAvailableFlag() {
            return availableFlag;
        }

        private String getRequiredRoomId() {
            return requiredRoomId;
        }

        private String getMissingChoiceMessage() {
            return missingChoiceMessage != null ? missingChoiceMessage : "Choose what?";
        }

        private String getUnavailableMessage() {
            return unavailableMessage != null ? unavailableMessage : "There is no major decision to make right now.";
        }

        private String getWrongRoomMessage() {
            return wrongRoomMessage != null ? wrongRoomMessage : "This choice cannot be made here.";
        }

        private String getInvalidChoiceMessage() {
            return invalidChoiceMessage != null ? invalidChoiceMessage : "That is not an available choice.";
        }

        private List<ChoiceOption> getChoices() {
            if (choices == null) {
                choices = new ArrayList<>();
            }

            return choices;
        }
    }

    private static class ChoiceOption {
        private String value;
        private List<String> aliases;
        private List<String> addItemIds;
        private String missingItemMessage;
        private Map<String, Boolean> setFlags;
        private List<ExitUnlock> unlockExits;
        private List<String> messages;

        private boolean matches(String input) {
            if (value != null && value.equalsIgnoreCase(input)) {
                return true;
            }

            for (String alias : getAliases()) {
                if (alias.equalsIgnoreCase(input)) {
                    return true;
                }
            }

            return false;
        }

        private List<String> getAliases() {
            if (aliases == null) {
                aliases = new ArrayList<>();
            }

            return aliases;
        }

        private List<String> getAddItemIds() {
            if (addItemIds == null) {
                addItemIds = new ArrayList<>();
            }

            return addItemIds;
        }

        private String getMissingItemMessage() {
            return missingItemMessage != null ? missingItemMessage : "A configured item is missing from the game data.";
        }

        private Map<String, Boolean> getSetFlags() {
            if (setFlags == null) {
                setFlags = new HashMap<>();
            }

            return setFlags;
        }

        private List<ExitUnlock> getUnlockExits() {
            if (unlockExits == null) {
                unlockExits = new ArrayList<>();
            }

            return unlockExits;
        }

        private List<String> getMessages() {
            if (messages == null) {
                messages = new ArrayList<>();
            }

            return messages;
        }
    }

    private static class ExitUnlock {
        private String roomId;
        private String direction;
    }
}
