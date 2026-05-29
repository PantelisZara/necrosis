package engine.loader;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import engine.model.DialogueEntry;
import engine.model.Enemy;
import engine.model.EnemySpawn;
import engine.model.EncounterPhase;
import engine.model.Exit;
import engine.model.Interactable;
import engine.model.Item;
import engine.model.Npc;
import engine.model.Room;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLoader {

    private static final Gson GSON = new Gson();

    public static LoadedGameData loadGameData(String filePath) {
        if (isBlank(filePath)) {
            throw new IllegalStateException("Game data path is required.");
        }

        try (Reader reader = Files.newBufferedReader(Path.of(filePath))) {
            GameFileData data = GSON.fromJson(reader, GameFileData.class);
            if (data == null) {
                throw new IllegalStateException("Game data file is empty: " + filePath);
            }

            GameConfig gameConfig = requireGameConfig(data);
            List<String> introLines = data.intro != null ? data.intro : new ArrayList<>();
            Map<String, Item> allItems = buildItemTemplates(data.items);
            Map<String, Room> rooms = buildRooms(data.rooms, allItems);
            validateStartRoom(gameConfig, rooms);
            validateExitTargets(rooms);
            List<EncounterPhase> encounterPhases = buildEncounterPhases(data.encounterPhases);

            return new LoadedGameData(rooms, encounterPhases, introLines, gameConfig, allItems);

        } catch (JsonSyntaxException e) {
            throw new IllegalStateException("Invalid JSON in game data file " + filePath + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read game data file " + filePath + ": " + e.getMessage(), e);
        }
    }

    private static Map<String, Item> buildItemTemplates(List<ItemData> itemList) {
        Map<String, Item> allItems = new HashMap<>();

        if (itemList == null) {
            return allItems;
        }

        for (int i = 0; i < itemList.size(); i++) {
            ItemData itemData = requireObject(itemList.get(i), "items[" + i + "]");
            String itemId = requireText(itemData.id, "items[" + i + "].id");
            String itemName = requireText(itemData.name, "items[" + i + "].name");
            String description = requireText(itemData.description, "items[" + i + "].description");

            if (allItems.containsKey(itemId)) {
                throw new IllegalStateException("Duplicate item id in game data: " + itemId);
            }

            boolean isPortable = itemData.isPortable == null || itemData.isPortable;
            allItems.put(
                    itemId,
                    new Item(itemId, itemName, description, isPortable)
            );
        }

        return allItems;
    }

    private static Map<String, Room> buildRooms(List<RoomData> roomList, Map<String, Item> allItems) {
        Map<String, Room> rooms = new HashMap<>();

        if (roomList == null || roomList.isEmpty()) {
            throw new IllegalStateException("Game data must define at least one room.");
        }

        for (int i = 0; i < roomList.size(); i++) {
            RoomData roomData = requireObject(roomList.get(i), "rooms[" + i + "]");
            String roomId = requireText(roomData.id, "rooms[" + i + "].id");
            String description = requireText(roomData.description, "rooms[" + i + "].description");

            if (rooms.containsKey(roomId)) {
                throw new IllegalStateException("Duplicate room id in game data: " + roomId);
            }

            rooms.put(roomId, new Room(
                    roomId,
                    description,
                    buildExits(roomId, roomData.exits),
                    buildRoomItems(roomId, roomData.items, allItems),
                    buildEnemies(roomId, roomData.enemies),
                    buildInteractables(roomId, roomData.interactables),
                    buildNpcs(roomId, roomData.npcs),
                    buildRoomHints(roomData.hint, roomData.hints)
            ));
        }

        return rooms;
    }

    private static List<String> buildRoomHints(String hint, List<String> hints) {
        List<String> roomHints = new ArrayList<>();

        if (hint != null && !hint.isBlank()) {
            roomHints.add(hint);
        }

        if (hints != null) {
            for (String entry : hints) {
                if (entry != null && !entry.isBlank()) {
                    roomHints.add(entry);
                }
            }
        }

        return roomHints;
    }

    private static Map<String, Exit> buildExits(String roomId, Map<String, ExitData> exitData) {
        Map<String, Exit> exits = new HashMap<>();

        if (exitData == null) {
            return exits;
        }

        for (Map.Entry<String, ExitData> entry : exitData.entrySet()) {
            String direction = requireText(entry.getKey(), "rooms['" + roomId + "'].exits direction");
            ExitData exit = requireObject(
                    entry.getValue(),
                    "rooms['" + roomId + "'].exits['" + direction + "']"
            );
            String targetRoomId = requireText(
                    exit.targetRoomId,
                    "rooms['" + roomId + "'].exits['" + direction + "'].targetRoomId"
            );
            boolean locked = exit.locked != null && exit.locked;
            exits.put(direction, new Exit(targetRoomId, locked));
        }

        return exits;
    }

    private static List<Item> buildRoomItems(String roomId, List<String> itemIds, Map<String, Item> allItems) {
        List<Item> roomItems = new ArrayList<>();

        if (itemIds == null) {
            return roomItems;
        }

        for (int i = 0; i < itemIds.size(); i++) {
            String itemId = requireText(itemIds.get(i), "rooms['" + roomId + "'].items[" + i + "]");
            Item item = allItems.get(itemId);
            if (item == null) {
                throw new IllegalStateException("Room '" + roomId + "' references unknown item: " + itemId);
            }

            roomItems.add(item);
        }

        return roomItems;
    }

    private static List<Enemy> buildEnemies(String roomId, List<EnemyData> enemyList) {
        List<Enemy> enemies = new ArrayList<>();

        if (enemyList == null) {
            return enemies;
        }

        for (int i = 0; i < enemyList.size(); i++) {
            EnemyData enemyData = requireObject(enemyList.get(i), "rooms['" + roomId + "'].enemies[" + i + "]");
            enemies.add(new Enemy(
                    requireText(enemyData.id, "rooms['" + roomId + "'].enemies[" + i + "].id"),
                    requireText(enemyData.name, "rooms['" + roomId + "'].enemies[" + i + "].name"),
                    requireText(enemyData.description, "rooms['" + roomId + "'].enemies[" + i + "].description"),
                    requireText(enemyData.type, "rooms['" + roomId + "'].enemies[" + i + "].type")
            ));
        }

        return enemies;
    }

    private static List<Interactable> buildInteractables(String roomId, List<InteractableData> interactableList) {
        List<Interactable> interactables = new ArrayList<>();

        if (interactableList == null) {
            return interactables;
        }

        for (int i = 0; i < interactableList.size(); i++) {
            InteractableData inter = requireObject(
                    interactableList.get(i),
                    "rooms['" + roomId + "'].interactables[" + i + "]"
            );
            interactables.add(new Interactable(
                    requireText(inter.id, "rooms['" + roomId + "'].interactables[" + i + "].id"),
                    requireText(inter.name, "rooms['" + roomId + "'].interactables[" + i + "].name"),
                    requireText(inter.description, "rooms['" + roomId + "'].interactables[" + i + "].description"),
                    inter.requiredItemId,
                    inter.requiredFlag,
                    inter.forbiddenFlag,
                    inter.setsFlag,
                    requireText(
                            inter.successMessage,
                            "rooms['" + roomId + "'].interactables[" + i + "].successMessage"
                    ),
                    inter.failureMessage,
                    inter.forbiddenMessage
            ));
        }

        return interactables;
    }

    private static List<Npc> buildNpcs(String roomId, List<NpcData> npcList) {
        List<Npc> npcs = new ArrayList<>();

        if (npcList == null) {
            return npcs;
        }

        for (int i = 0; i < npcList.size(); i++) {
            NpcData npcData = requireObject(npcList.get(i), "rooms['" + roomId + "'].npcs[" + i + "]");
            List<DialogueEntry> dialogues = new ArrayList<>();

            if (npcData.dialogues != null) {
                for (int j = 0; j < npcData.dialogues.size(); j++) {
                    DialogueData dialogueData = requireObject(
                            npcData.dialogues.get(j),
                            "rooms['" + roomId + "'].npcs[" + i + "].dialogues[" + j + "]"
                    );
                    dialogues.add(new DialogueEntry(
                            dialogueData.requiredFlag,
                            dialogueData.forbiddenFlag,
                            requireText(
                                    dialogueData.text,
                                    "rooms['" + roomId + "'].npcs[" + i + "].dialogues[" + j + "].text"
                            )
                    ));
                }
            }

            npcs.add(new Npc(
                    requireText(npcData.id, "rooms['" + roomId + "'].npcs[" + i + "].id"),
                    requireText(npcData.name, "rooms['" + roomId + "'].npcs[" + i + "].name"),
                    requireText(npcData.description, "rooms['" + roomId + "'].npcs[" + i + "].description"),
                    dialogues
            ));
        }

        return npcs;
    }

    private static List<EncounterPhase> buildEncounterPhases(List<EncounterPhaseData> phaseList) {
        List<EncounterPhase> encounterPhases = new ArrayList<>();

        if (phaseList == null) {
            return encounterPhases;
        }

        for (int i = 0; i < phaseList.size(); i++) {
            EncounterPhaseData phaseData = requireObject(phaseList.get(i), "encounterPhases[" + i + "]");
            List<EnemySpawn> spawns = new ArrayList<>();

            if (phaseData.enemies != null) {
                for (int j = 0; j < phaseData.enemies.size(); j++) {
                    EnemySpawnData enemyData = requireObject(
                            phaseData.enemies.get(j),
                            "encounterPhases[" + i + "].enemies[" + j + "]"
                    );
                    int count = requirePositive(enemyData.count, "encounterPhases[" + i + "].enemies[" + j + "].count");
                    spawns.add(new EnemySpawn(
                            requireText(enemyData.type, "encounterPhases[" + i + "].enemies[" + j + "].type"),
                            count
                    ));
                }
            }

            encounterPhases.add(new EncounterPhase(
                    requirePositive(phaseData.phase, "encounterPhases[" + i + "].phase"),
                    requireText(phaseData.message, "encounterPhases[" + i + "].message"),
                    spawns
            ));
        }

        return encounterPhases;
    }

    private static GameConfig requireGameConfig(GameFileData data) {
        if (data.gameConfig == null) {
            throw new IllegalStateException("Missing required object: gameConfig.");
        }

        requireText(data.gameConfig.getStartRoomId(), "gameConfig.startRoomId");
        validateCommandDefinitions(data.gameConfig.getCommands());
        return data.gameConfig;
    }

    private static void validateCommandDefinitions(List<CommandDefinition> commandDefinitions) {
        if (commandDefinitions == null || commandDefinitions.isEmpty()) {
            throw new IllegalStateException("gameConfig.commands must define at least one command.");
        }

        for (int i = 0; i < commandDefinitions.size(); i++) {
            CommandDefinition definition = requireObject(
                    commandDefinitions.get(i),
                    "gameConfig.commands[" + i + "]"
            );
            requireText(definition.getClassName(), "gameConfig.commands[" + i + "].className");

            List<String> aliases = definition.getAliases();
            if (aliases.isEmpty()) {
                throw new IllegalStateException("gameConfig.commands[" + i + "].aliases must define at least one alias.");
            }

            for (int j = 0; j < aliases.size(); j++) {
                requireText(aliases.get(j), "gameConfig.commands[" + i + "].aliases[" + j + "]");
            }
        }
    }

    private static void validateStartRoom(GameConfig gameConfig, Map<String, Room> rooms) {
        String startRoomId = gameConfig.getStartRoomId();
        if (!rooms.containsKey(startRoomId)) {
            throw new IllegalStateException("gameConfig.startRoomId references unknown room: " + startRoomId);
        }
    }

    private static void validateExitTargets(Map<String, Room> rooms) {
        for (Room room : rooms.values()) {
            for (Map.Entry<String, Exit> entry : room.getExits().entrySet()) {
                String targetRoomId = entry.getValue().getTargetRoomId();
                if (!rooms.containsKey(targetRoomId)) {
                    throw new IllegalStateException(
                            "Room '" + room.getId() + "' exit '" + entry.getKey()
                                    + "' references unknown room: " + targetRoomId
                    );
                }
            }
        }
    }

    private static <T> T requireObject(T value, String fieldName) {
        if (value == null) {
            throw new IllegalStateException("Missing required object: " + fieldName + ".");
        }

        return value;
    }

    private static String requireText(String value, String fieldName) {
        if (isBlank(value)) {
            throw new IllegalStateException("Missing required field: " + fieldName + ".");
        }

        return value;
    }

    private static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalStateException("Field must be positive: " + fieldName + ".");
        }

        return value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static class GameFileData {
        private List<String> intro;
        private List<RoomData> rooms;
        private List<ItemData> items;
        private List<EncounterPhaseData> encounterPhases;
        private GameConfig gameConfig;
    }

    private static class RoomData {
        private String id;
        private String description;
        private Map<String, ExitData> exits;
        private List<String> items;
        private List<EnemyData> enemies;
        private List<InteractableData> interactables;
        private List<NpcData> npcs;
        private String hint;
        private List<String> hints;
    }

    private static class ExitData {
        private String targetRoomId;
        private Boolean locked;
    }

    private static class ItemData {
        private String id;
        private String name;
        private String description;
        @SerializedName("is_portable")
        private Boolean isPortable;
    }

    private static class EnemyData {
        private String id;
        private String name;
        private String description;
        private String type;
    }

    private static class InteractableData {
        private String id;
        private String name;
        private String description;
        private String requiredItemId;
        private String requiredFlag;
        private String forbiddenFlag;
        private String setsFlag;
        private String successMessage;
        private String failureMessage;
        private String forbiddenMessage;
    }

    private static class NpcData {
        private String id;
        private String name;
        private String description;
        private List<DialogueData> dialogues;
    }

    private static class DialogueData {
        private String requiredFlag;
        private String forbiddenFlag;
        private String text;
    }

    private static class EncounterPhaseData {
        private int phase;
        private String message;
        private List<EnemySpawnData> enemies;
    }

    private static class EnemySpawnData {
        private String type;
        private int count;
    }
}
