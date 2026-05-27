package engine.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import engine.core.CurrentGameState;
import engine.model.Exit;
import engine.model.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterCommand implements InterfaceCommand, ConfigurableCommand {

    private static final Gson GSON = new Gson();

    private EnterConfig config = new EnterConfig();

    @Override
    public void configure(JsonObject configJson) {
        if (configJson == null) {
            return;
        }

        EnterConfig parsedConfig = GSON.fromJson(configJson, EnterConfig.class);
        if (parsedConfig != null) {
            config = parsedConfig;
        }
    }

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {

        if (config.getRequiredFlag() != null && !gameState.isFlagTrue(config.getRequiredFlag())) {
            System.out.println(config.getInactiveMessage());
            return;
        }

        if (args == null || args.isEmpty()) {
            System.out.println(config.getMissingInputMessage());
            return;
        }

        String enteredCode = args.get(args.size() - 1);

        if (config.getCode() != null && !config.getCode().equals(enteredCode)) {
            System.out.println(config.getDeniedMessage());
            return;
        }

        for (String message : config.getSuccessMessages()) {
            System.out.println(message);
        }

        for (Map.Entry<String, Boolean> entry : config.getSetFlags().entrySet()) {
            gameState.setFlag(entry.getKey(), entry.getValue());
        }

        for (ExitUnlock unlock : config.getUnlockExits()) {
            Room room = gameState.getRoom(unlock.roomId);
            if (room == null) {
                continue;
            }

            Exit exit = room.getExit(unlock.direction);
            if (exit != null) {
                exit.unlock();
            }
        }
    }

    private static class EnterConfig {
        private String requiredFlag;
        private String code;
        private String inactiveMessage;
        private String missingInputMessage;
        private String deniedMessage;
        private List<String> successMessages;
        private Map<String, Boolean> setFlags;
        private List<ExitUnlock> unlockExits;

        private String getRequiredFlag() {
            return requiredFlag;
        }

        private String getCode() {
            return code;
        }

        private String getInactiveMessage() {
            return inactiveMessage != null ? inactiveMessage : "There is nothing to enter a code into.";
        }

        private String getMissingInputMessage() {
            return missingInputMessage != null ? missingInputMessage : "Enter what code?";
        }

        private String getDeniedMessage() {
            return deniedMessage != null ? deniedMessage : "ACCESS DENIED.";
        }

        private List<String> getSuccessMessages() {
            if (successMessages == null) {
                successMessages = new ArrayList<>();
            }

            return successMessages;
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
    }

    private static class ExitUnlock {
        private String roomId;
        private String direction;
    }
}
