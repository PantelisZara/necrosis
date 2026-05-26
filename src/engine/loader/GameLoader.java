package engine.loader;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import engine.model.DialogueEntry;
import engine.model.Enemy;
import engine.model.EnemySpawn;
import engine.model.EnemyType;
import engine.model.Exit;
import engine.model.Interactable;
import engine.model.Item;
import engine.model.Npc;
import engine.model.Room;
import engine.model.ZaunPhase;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLoader {

    public static LoadedGameData loadGameData(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            GameFileData data = gson.fromJson(reader, GameFileData.class);

            if (data == null) {
                return LoadedGameData.empty();
            }

            List<String> introLines = data.intro != null ? data.intro : new ArrayList<>();
            Map<String, Item> allItems = buildItemTemplates(data.items);
            Map<String, Room> rooms = buildRooms(data.rooms, allItems);
            List<ZaunPhase> zaunPhases = buildZaunPhases(data.zaunPhases);
            GameConfig gameConfig = data.gameConfig != null ? data.gameConfig : new GameConfig();

            return new LoadedGameData(rooms, zaunPhases, introLines, gameConfig, allItems);

        } catch (Exception e) {
            e.printStackTrace();
            return LoadedGameData.empty();
        }
    }

    private static Map<String, Item> buildItemTemplates(List<ItemData> itemList) {
        Map<String, Item> allItems = new HashMap<>();

        if (itemList == null) {
            return allItems;
        }

        for (ItemData itemData : itemList) {
            boolean isPortable = itemData.isPortable == null || itemData.isPortable;
            allItems.put(
                    itemData.id,
                    new Item(itemData.id, itemData.name, itemData.description, isPortable)
            );
        }

        return allItems;
    }

    private static Map<String, Room> buildRooms(List<RoomData> roomList, Map<String, Item> allItems) {
        Map<String, Room> rooms = new HashMap<>();

        if (roomList == null) {
            return rooms;
        }

        for (RoomData roomData : roomList) {
            rooms.put(roomData.id, new Room(
                    roomData.id,
                    roomData.description,
                    buildExits(roomData.exits),
                    buildRoomItems(roomData.items, allItems),
                    buildEnemies(roomData.enemies),
                    buildInteractables(roomData.interactables),
                    buildNpcs(roomData.npcs)
            ));
        }

        return rooms;
    }

    private static Map<String, Exit> buildExits(Map<String, ExitData> exitData) {
        Map<String, Exit> exits = new HashMap<>();

        if (exitData == null) {
            return exits;
        }

        for (Map.Entry<String, ExitData> entry : exitData.entrySet()) {
            ExitData exit = entry.getValue();
            boolean locked = exit.locked != null && exit.locked;
            exits.put(entry.getKey(), new Exit(exit.targetRoomId, locked));
        }

        return exits;
    }

    private static List<Item> buildRoomItems(List<String> itemIds, Map<String, Item> allItems) {
        List<Item> roomItems = new ArrayList<>();

        if (itemIds == null) {
            return roomItems;
        }

        for (String itemId : itemIds) {
            Item item = allItems.get(itemId);
            if (item != null) {
                roomItems.add(item);
            }
        }

        return roomItems;
    }

    private static List<Enemy> buildEnemies(List<EnemyData> enemyList) {
        List<Enemy> enemies = new ArrayList<>();

        if (enemyList == null) {
            return enemies;
        }

        for (EnemyData enemyData : enemyList) {
            enemies.add(new Enemy(
                    enemyData.id,
                    enemyData.name,
                    enemyData.description,
                    EnemyType.valueOf(enemyData.type.toUpperCase())
            ));
        }

        return enemies;
    }

    private static List<Interactable> buildInteractables(List<InteractableData> interactableList) {
        List<Interactable> interactables = new ArrayList<>();

        if (interactableList == null) {
            return interactables;
        }

        for (InteractableData inter : interactableList) {
            interactables.add(new Interactable(
                    inter.id,
                    inter.name,
                    inter.description,
                    inter.requiredItemId,
                    inter.requiredFlag,
                    inter.setsFlag,
                    inter.successMessage,
                    inter.failureMessage
            ));
        }

        return interactables;
    }

    private static List<Npc> buildNpcs(List<NpcData> npcList) {
        List<Npc> npcs = new ArrayList<>();

        if (npcList == null) {
            return npcs;
        }

        for (NpcData npcData : npcList) {
            List<DialogueEntry> dialogues = new ArrayList<>();

            if (npcData.dialogues != null) {
                for (DialogueData dialogueData : npcData.dialogues) {
                    dialogues.add(new DialogueEntry(
                            dialogueData.requiredFlag,
                            dialogueData.forbiddenFlag,
                            dialogueData.text
                    ));
                }
            }

            npcs.add(new Npc(npcData.id, npcData.name, npcData.description, dialogues));
        }

        return npcs;
    }

    private static List<ZaunPhase> buildZaunPhases(List<ZaunPhaseData> phaseList) {
        List<ZaunPhase> zaunPhases = new ArrayList<>();

        if (phaseList == null) {
            return zaunPhases;
        }

        for (ZaunPhaseData phaseData : phaseList) {
            List<EnemySpawn> spawns = new ArrayList<>();

            if (phaseData.enemies != null) {
                for (EnemySpawnData enemyData : phaseData.enemies) {
                    spawns.add(new EnemySpawn(enemyData.type, enemyData.count));
                }
            }

            zaunPhases.add(new ZaunPhase(phaseData.phase, phaseData.message, spawns));
        }

        return zaunPhases;
    }

    private static class GameFileData {
        private List<String> intro;
        private List<RoomData> rooms;
        private List<ItemData> items;
        @SerializedName("zaun_phases")
        private List<ZaunPhaseData> zaunPhases;
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
        private String setsFlag;
        private String successMessage;
        private String failureMessage;
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

    private static class ZaunPhaseData {
        private int phase;
        private String message;
        private List<EnemySpawnData> enemies;
    }

    private static class EnemySpawnData {
        private String type;
        private int count;
    }
}
