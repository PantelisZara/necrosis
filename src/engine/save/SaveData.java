package engine.save;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveData {

    private String currentRoomId;
    private List<String> inventoryItemIds;
    private Map<String, Boolean> flags;
    private int zaunPhase;
    private List<String> commandHistory;
    private Map<String, List<String>> roomItemIds;
    private Map<String, List<SavedEnemy>> roomEnemies;
    private Map<String, Map<String, Boolean>> exitLocks;
    private boolean playerInjured;
    private int playerTurnsUntilDeath;
    private boolean playerAlive;

    public SaveData() {
        this.inventoryItemIds = new ArrayList<>();
        this.flags = new HashMap<>();
        this.commandHistory = new ArrayList<>();
        this.roomItemIds = new HashMap<>();
        this.roomEnemies = new HashMap<>();
        this.exitLocks = new HashMap<>();
        this.playerAlive = true;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public List<String> getInventoryItemIds() {
        return inventoryItemIds;
    }

    public void setInventoryItemIds(List<String> inventoryItemIds) {
        this.inventoryItemIds = inventoryItemIds;
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public void setFlags(Map<String, Boolean> flags) {
        this.flags = flags;
    }

    public int getZaunPhase() {
        return zaunPhase;
    }

    public void setZaunPhase(int zaunPhase) {
        this.zaunPhase = zaunPhase;
    }

    public List<String> getCommandHistory() {
        return commandHistory;
    }

    public void setCommandHistory(List<String> commandHistory) {
        this.commandHistory = commandHistory;
    }

    public Map<String, List<String>> getRoomItemIds() {
        return roomItemIds;
    }

    public void setRoomItemIds(Map<String, List<String>> roomItemIds) {
        this.roomItemIds = roomItemIds;
    }

    public Map<String, List<SavedEnemy>> getRoomEnemies() {
        return roomEnemies;
    }

    public void setRoomEnemies(Map<String, List<SavedEnemy>> roomEnemies) {
        this.roomEnemies = roomEnemies;
    }

    public Map<String, Map<String, Boolean>> getExitLocks() {
        return exitLocks;
    }

    public void setExitLocks(Map<String, Map<String, Boolean>> exitLocks) {
        this.exitLocks = exitLocks;
    }

    public boolean isPlayerInjured() {
        return playerInjured;
    }

    public void setPlayerInjured(boolean playerInjured) {
        this.playerInjured = playerInjured;
    }

    public int getPlayerTurnsUntilDeath() {
        return playerTurnsUntilDeath;
    }

    public void setPlayerTurnsUntilDeath(int playerTurnsUntilDeath) {
        this.playerTurnsUntilDeath = playerTurnsUntilDeath;
    }

    public boolean isPlayerAlive() {
        return playerAlive;
    }

    public void setPlayerAlive(boolean playerAlive) {
        this.playerAlive = playerAlive;
    }

    public static class SavedEnemy {
        private String id;
        private String name;
        private String description;
        private String type;
        private boolean defeated;

        public SavedEnemy() {
        }

        public SavedEnemy(String id, String name, String description, String type, boolean defeated) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.type = type;
            this.defeated = defeated;
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
