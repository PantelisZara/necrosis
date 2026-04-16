package engine.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import engine.model.*;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

public class GameLoader {

    public static LoadedGameData loadGameData(String filePath) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(filePath);

            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> data = gson.fromJson(reader, type);

            Map<String, Room> rooms = new HashMap<>();

            List<Map<String, Object>> roomList = (List<Map<String, Object>>) data.get("rooms");
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) data.get("items");
            List<Map<String, Object>> phaseList = (List<Map<String, Object>>) data.get("zaun_phases");


            Map<String, Item> allItems = new HashMap<>();

            if (itemList != null) {
                for (Map<String, Object> i : itemList) {
                    String id = (String) i.get("id");
                    String name = (String) i.get("name");
                    String desc = (String) i.get("description");

                    Boolean portableValue = (Boolean) i.get("is_portable");
                    boolean isPortable = portableValue != null ? portableValue : true;

                    allItems.put(id, new Item(id, name, desc, isPortable));
                }
            }


            if (roomList != null) {
                for (Map<String, Object> r : roomList) {
                    String id = (String) r.get("id");
                    String desc = (String) r.get("description");

                    // EXITS
                    Map<String, Exit> exits = new HashMap<>();
                    Map<String, Object> rawExits = (Map<String, Object>) r.get("exits");

                    if (rawExits != null) {
                        for (Map.Entry<String, Object> entry : rawExits.entrySet()) {
                            String direction = entry.getKey();
                            Map<String, Object> exitData = (Map<String, Object>) entry.getValue();

                            String targetRoomId = (String) exitData.get("targetRoomId");
                            Boolean lockedValue = (Boolean) exitData.get("locked");
                            boolean locked = lockedValue != null && lockedValue;

                            exits.put(direction, new Exit(targetRoomId, locked));
                        }
                    }


                    List<Item> roomItems = new ArrayList<>();
                    List<String> itemIds = (List<String>) r.get("items");

                    if (itemIds != null) {
                        for (String itemId : itemIds) {
                            Item item = allItems.get(itemId);
                            if (item != null) {
                                roomItems.add(item);
                            }
                        }
                    }


                    List<Enemy> roomEnemies = new ArrayList<>();
                    List<Map<String, Object>> enemyList = (List<Map<String, Object>>) r.get("enemies");

                    if (enemyList != null) {
                        for (Map<String, Object> e : enemyList) {
                            String enemyId = (String) e.get("id");
                            String enemyName = (String) e.get("name");
                            String enemyDescription = (String) e.get("description");
                            String enemyTypeString = (String) e.get("type");

                            EnemyType enemyType = EnemyType.valueOf(enemyTypeString.toUpperCase());

                            roomEnemies.add(new Enemy(enemyId, enemyName, enemyDescription, enemyType));
                        }
                    }


                    List<Interactable> roomInteractables = new ArrayList<>();
                    List<Map<String, Object>> interactableList =
                            (List<Map<String, Object>>) r.get("interactables");

                    if (interactableList != null) {
                        for (Map<String, Object> inter : interactableList) {
                            String interactableId = (String) inter.get("id");
                            String interactableName = (String) inter.get("name");
                            String interactableDescription = (String) inter.get("description");
                            String requiredItemId = (String) inter.get("requiredItemId");
                            String requiredFlag = (String) inter.get("requiredFlag");
                            String setsFlag = (String) inter.get("setsFlag");
                            String successMessage = (String) inter.get("successMessage");
                            String failureMessage = (String) inter.get("failureMessage");

                            roomInteractables.add(new Interactable(
                                    interactableId,
                                    interactableName,
                                    interactableDescription,
                                    requiredItemId,
                                    requiredFlag,
                                    setsFlag,
                                    successMessage,
                                    failureMessage
                            ));
                        }
                    }


                    List<Npc> roomNpcs = new ArrayList<>();
                    List<Map<String, Object>> npcList = (List<Map<String, Object>>) r.get("npcs");

                    if (npcList != null) {
                        for (Map<String, Object> npcData : npcList) {
                            String npcId = (String) npcData.get("id");
                            String npcName = (String) npcData.get("name");
                            String npcDescription = (String) npcData.get("description");

                            List<DialogueEntry> dialogues = new ArrayList<>();
                            List<Map<String, Object>> dialogueList =
                                    (List<Map<String, Object>>) npcData.get("dialogues");

                            if (dialogueList != null) {
                                for (Map<String, Object> dialogueData : dialogueList) {
                                    String requiredFlag = (String) dialogueData.get("requiredFlag");
                                    String forbiddenFlag = (String) dialogueData.get("forbiddenFlag");
                                    String textValue = (String) dialogueData.get("text");

                                    dialogues.add(new DialogueEntry(requiredFlag, forbiddenFlag, textValue));
                                }
                            }

                            roomNpcs.add(new Npc(npcId, npcName, npcDescription, dialogues));
                        }
                    }

                    rooms.put(id, new Room(
                            id,
                            desc,
                            exits,
                            roomItems,
                            roomEnemies,
                            roomInteractables,
                            roomNpcs
                    ));
                }
            }


            List<ZaunPhase> zaunPhases = new ArrayList<>();

            if (phaseList != null) {
                for (Map<String, Object> p : phaseList) {
                    int phaseNumber = ((Double) p.get("phase")).intValue();
                    String message = (String) p.get("message");

                    List<EnemySpawn> spawns = new ArrayList<>();
                    List<Map<String, Object>> phaseEnemies =
                            (List<Map<String, Object>>) p.get("enemies");

                    if (phaseEnemies != null) {
                        for (Map<String, Object> e : phaseEnemies) {
                            String enemyType = (String) e.get("type");
                            int count = ((Double) e.get("count")).intValue();

                            spawns.add(new EnemySpawn(enemyType, count));
                        }
                    }

                    zaunPhases.add(new ZaunPhase(phaseNumber, message, spawns));
                }
            }

            return new LoadedGameData(rooms, zaunPhases);

        } catch (Exception e) {
            e.printStackTrace();
            return new LoadedGameData(new HashMap<>(), new ArrayList<>());
        }
    }
}