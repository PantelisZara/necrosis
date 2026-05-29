package engine.save;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveData {

    private Integer saveFormatVersion;
    private String gameDataPath;
    private String currentRoomId;
    private List<String> inventoryItemIds;
    private Map<String, Boolean> flags;
    private Integer encounterPhase;
    private List<String> commandHistory;
    private Map<String, List<String>> roomItemIds;
    private Map<String, List<SavedEnemy>> roomEnemies;
    private Map<String, Map<String, Boolean>> exitLocks;
    private Boolean playerInjured;
    private Integer playerTurnsUntilDeath;
    private Boolean playerAlive;

    public SaveData() {
        this.commandHistory = new ArrayList<>();
    }

    public int getSaveFormatVersion() {
        return saveFormatVersion != null ? saveFormatVersion : 1;
    }

    public void setSaveFormatVersion(int saveFormatVersion) {
        this.saveFormatVersion = saveFormatVersion;
    }

    public String getGameDataPath() {
        return gameDataPath;
    }

    public void setGameDataPath(String gameDataPath) {
        this.gameDataPath = gameDataPath;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public List<String> getInventoryItemIds() {
        return inventoryItemIds;
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public int getEncounterPhase() {
        return encounterPhase != null ? encounterPhase : 0;
    }

    public List<String> getCommandHistory() {
        if (commandHistory == null) {
            commandHistory = new ArrayList<>();
        }

        return commandHistory;
    }

    public void setCommandHistory(List<String> commandHistory) {
        this.commandHistory = commandHistory;
    }

    public Map<String, List<String>> getRoomItemIds() {
        return roomItemIds;
    }

    public Map<String, List<SavedEnemy>> getRoomEnemies() {
        return roomEnemies;
    }

    public Map<String, Map<String, Boolean>> getExitLocks() {
        return exitLocks;
    }

    public boolean isPlayerInjured() {
        return Boolean.TRUE.equals(playerInjured);
    }

    public int getPlayerTurnsUntilDeath() {
        return playerTurnsUntilDeath != null ? playerTurnsUntilDeath : 0;
    }

    public boolean isPlayerAlive() {
        return playerAlive == null || playerAlive;
    }

    public boolean hasReplayHistory() {
        return commandHistory != null && !commandHistory.isEmpty();
    }

    public boolean hasLegacySnapshot() {
        return currentRoomId != null
                || inventoryItemIds != null
                || flags != null
                || encounterPhase != null
                || roomItemIds != null
                || roomEnemies != null
                || exitLocks != null
                || playerInjured != null
                || playerTurnsUntilDeath != null
                || playerAlive != null;
    }

    public static class SavedEnemy {
        private String id;
        private String name;
        private String description;
        private String type;
        private boolean defeated;

        public SavedEnemy() {
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getType() {
            return type;
        }

        public boolean isDefeated() {
            return defeated;
        }
    }
}
