package engine.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.core.CurrentGameState;
import engine.model.Enemy;
import engine.model.EnemyType;
import engine.model.Exit;
import engine.model.Item;
import engine.model.Room;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveManager {

    private static final Path SAVE_DIRECTORY = Path.of("saves");
    private static final Path SAVE_FILE = SAVE_DIRECTORY.resolve("savegame.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String getSavePath() {
        return SAVE_FILE.toString();
    }

    public static boolean saveExists() {
        return Files.exists(SAVE_FILE);
    }

    public static void save(CurrentGameState gameState) throws IOException {
        Files.createDirectories(SAVE_DIRECTORY);

        try (Writer writer = Files.newBufferedWriter(SAVE_FILE)) {
            GSON.toJson(toSaveData(gameState), writer);
        }
    }

    public static void load(CurrentGameState gameState) throws IOException {
        try (Reader reader = Files.newBufferedReader(SAVE_FILE)) {
            SaveData saveData = GSON.fromJson(reader, SaveData.class);
            restore(gameState, saveData);
        }
    }

    private static SaveData toSaveData(CurrentGameState gameState) {
        SaveData saveData = new SaveData();

        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        if (currentRoom != null) {
            saveData.setCurrentRoomId(currentRoom.getId());
        }

        saveData.setInventoryItemIds(getInventoryItemIds(gameState));
        saveData.setFlags(new HashMap<>(gameState.getFlags()));
        saveData.setZaunPhase(gameState.getZaunPhase());
        saveData.setCommandHistory(gameState.getCommandHistory());
        saveData.setRoomItemIds(getRoomItemIds(gameState));
        saveData.setRoomEnemies(getRoomEnemies(gameState));
        saveData.setExitLocks(getExitLocks(gameState));
        saveData.setPlayerInjured(gameState.getPlayer().isInjured());
        saveData.setPlayerTurnsUntilDeath(gameState.getPlayer().getTurnsUntilDeath());
        saveData.setPlayerAlive(gameState.getPlayer().isAlive());

        return saveData;
    }

    private static List<String> getInventoryItemIds(CurrentGameState gameState) {
        List<String> itemIds = new ArrayList<>();

        for (Item item : gameState.getPlayer().getInventory()) {
            itemIds.add(item.getId());
        }

        return itemIds;
    }

    private static Map<String, List<String>> getRoomItemIds(CurrentGameState gameState) {
        Map<String, List<String>> roomItemIds = new HashMap<>();

        for (Room room : gameState.getRooms().values()) {
            List<String> itemIds = new ArrayList<>();

            for (Item item : room.getItems()) {
                itemIds.add(item.getId());
            }

            roomItemIds.put(room.getId(), itemIds);
        }

        return roomItemIds;
    }

    private static Map<String, List<SaveData.SavedEnemy>> getRoomEnemies(CurrentGameState gameState) {
        Map<String, List<SaveData.SavedEnemy>> roomEnemies = new HashMap<>();

        for (Room room : gameState.getRooms().values()) {
            List<SaveData.SavedEnemy> enemies = new ArrayList<>();

            for (Enemy enemy : room.getEnemies()) {
                enemies.add(new SaveData.SavedEnemy(
                        enemy.getId(),
                        enemy.getName(),
                        enemy.getDescription(),
                        enemy.getType().name(),
                        enemy.isDefeated()
                ));
            }

            roomEnemies.put(room.getId(), enemies);
        }

        return roomEnemies;
    }

    private static Map<String, Map<String, Boolean>> getExitLocks(CurrentGameState gameState) {
        Map<String, Map<String, Boolean>> exitLocks = new HashMap<>();

        for (Room room : gameState.getRooms().values()) {
            Map<String, Boolean> roomExitLocks = new HashMap<>();

            for (Map.Entry<String, Exit> entry : room.getExits().entrySet()) {
                roomExitLocks.put(entry.getKey(), entry.getValue().isLocked());
            }

            exitLocks.put(room.getId(), roomExitLocks);
        }

        return exitLocks;
    }

    private static void restore(CurrentGameState gameState, SaveData saveData) {
        if (saveData == null) {
            throw new IllegalStateException("Save file is empty or invalid.");
        }

        restoreRooms(gameState, saveData);
        restoreInventory(gameState, saveData);

        gameState.getFlags().clear();
        if (saveData.getFlags() != null) {
            gameState.getFlags().putAll(saveData.getFlags());
        }

        gameState.setZaunPhase(saveData.getZaunPhase());
        gameState.restoreCommandHistory(saveData.getCommandHistory());
        gameState.getPlayer().restoreHealthState(
                saveData.isPlayerInjured(),
                saveData.getPlayerTurnsUntilDeath(),
                saveData.isPlayerAlive()
        );

        Room savedRoom = gameState.getRoom(saveData.getCurrentRoomId());
        if (savedRoom == null) {
            throw new IllegalStateException("Saved room could not be found: " + saveData.getCurrentRoomId());
        }

        gameState.getPlayer().setCurrentRoom(savedRoom);
    }

    private static void restoreRooms(CurrentGameState gameState, SaveData saveData) {
        restoreRoomItems(gameState, saveData.getRoomItemIds());
        restoreRoomEnemies(gameState, saveData.getRoomEnemies());
        restoreExitLocks(gameState, saveData.getExitLocks());
    }

    private static void restoreRoomItems(CurrentGameState gameState, Map<String, List<String>> roomItemIds) {
        if (roomItemIds == null) {
            return;
        }

        for (Map.Entry<String, List<String>> entry : roomItemIds.entrySet()) {
            Room room = gameState.getRoom(entry.getKey());
            if (room == null) {
                continue;
            }

            room.getItems().clear();
            for (String itemId : entry.getValue()) {
                Item item = gameState.createItemById(itemId);
                if (item != null) {
                    room.getItems().add(item);
                }
            }
        }
    }

    private static void restoreInventory(CurrentGameState gameState, SaveData saveData) {
        gameState.getPlayer().getInventory().clear();

        if (saveData.getInventoryItemIds() == null) {
            return;
        }

        for (String itemId : saveData.getInventoryItemIds()) {
            Item item = gameState.createItemById(itemId);
            if (item != null) {
                gameState.getPlayer().addItem(item);
            }
        }
    }

    private static void restoreRoomEnemies(CurrentGameState gameState, Map<String, List<SaveData.SavedEnemy>> roomEnemies) {
        if (roomEnemies == null) {
            return;
        }

        for (Map.Entry<String, List<SaveData.SavedEnemy>> entry : roomEnemies.entrySet()) {
            Room room = gameState.getRoom(entry.getKey());
            if (room == null) {
                continue;
            }

            room.getEnemies().clear();
            for (SaveData.SavedEnemy savedEnemy : entry.getValue()) {
                Enemy enemy = new Enemy(
                        savedEnemy.getId(),
                        savedEnemy.getName(),
                        savedEnemy.getDescription(),
                        EnemyType.valueOf(savedEnemy.getType())
                );

                if (savedEnemy.isDefeated()) {
                    enemy.defeat();
                }

                room.getEnemies().add(enemy);
            }
        }
    }

    private static void restoreExitLocks(CurrentGameState gameState, Map<String, Map<String, Boolean>> exitLocks) {
        if (exitLocks == null) {
            return;
        }

        for (Map.Entry<String, Map<String, Boolean>> roomEntry : exitLocks.entrySet()) {
            Room room = gameState.getRoom(roomEntry.getKey());
            if (room == null) {
                continue;
            }

            for (Map.Entry<String, Boolean> exitEntry : roomEntry.getValue().entrySet()) {
                Exit exit = room.getExit(exitEntry.getKey());
                if (exit != null) {
                    exit.setLocked(Boolean.TRUE.equals(exitEntry.getValue()));
                }
            }
        }
    }
}
