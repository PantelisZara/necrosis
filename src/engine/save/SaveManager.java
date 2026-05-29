package engine.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.core.CurrentGameState;
import engine.model.Enemy;
import engine.model.Exit;
import engine.model.Item;
import engine.model.Room;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class SaveManager {

    public static final int CURRENT_SAVE_FORMAT_VERSION = 2;

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

    public static SaveData loadSaveData() throws IOException {
        try (Reader reader = Files.newBufferedReader(SAVE_FILE)) {
            return GSON.fromJson(reader, SaveData.class);
        }
    }

    private static SaveData toSaveData(CurrentGameState gameState) {
        SaveData saveData = new SaveData();
        saveData.setSaveFormatVersion(CURRENT_SAVE_FORMAT_VERSION);
        saveData.setGameDataPath(gameState.getGameDataPath());
        saveData.setCommandHistory(gameState.getCommandHistory());

        return saveData;
    }

    public static void restoreLegacySnapshot(CurrentGameState gameState, SaveData saveData) {
        if (saveData == null) {
            throw new IllegalStateException("Save file is empty or invalid.");
        }

        if (!saveData.hasLegacySnapshot()) {
            throw new IllegalStateException("Replay-based saves must be loaded through the game session.");
        }

        restoreRooms(gameState, saveData);
        restoreInventory(gameState, saveData);

        gameState.getFlags().clear();
        if (saveData.getFlags() != null) {
            gameState.getFlags().putAll(saveData.getFlags());
        }

        gameState.setEncounterPhase(saveData.getEncounterPhase());
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
                        savedEnemy.getType()
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
