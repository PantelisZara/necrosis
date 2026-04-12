package engine.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import engine.model.Exit;
import engine.model.Item;
import engine.model.Room;
import engine.model.Enemy;
import engine.model.EnemyType;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

public class GameLoader {

    public static Map<String, Room> loadRooms(String filePath) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(filePath);

            Type type = new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType();
            Map<String, List<Map<String, Object>>> data = gson.fromJson(reader, type);

            Map<String, Room> rooms = new HashMap<>();
            List<Map<String, Object>> roomList = data.get("rooms");
            List<Map<String, Object>> itemList = data.get("items");

            Map<String, Item> allItems = new HashMap<>();
            for (Map<String, Object> i : itemList) {
                String id = (String) i.get("id");
                String name = (String) i.get("name");
                String desc = (String) i.get("description");
                Boolean portableValue = (Boolean) i.get("is_portable");
                boolean isPortable = portableValue != null ? portableValue : true;

                allItems.put(id, new Item(id, name, desc, isPortable));
            }

            for (Map<String, Object> r : roomList) {
                String id = (String) r.get("id");
                String desc = (String) r.get("description");

                Map<String, Exit> exits = new HashMap<>();
                Map<String, Object> rawExits = (Map<String, Object>) r.get("exits");

                if (rawExits != null) {
                    for (Map.Entry<String, Object> entry : rawExits.entrySet()) {
                        String direction = entry.getKey();

                        Map<String, Object> exitData = (Map<String, Object>) entry.getValue();

                        String targetRoomId = (String) exitData.get("targetRoomId");
                        boolean locked = (Boolean) exitData.get("locked");

                        exits.put(direction, new Exit(targetRoomId, locked));
                    }
                }

                List<String> itemIds = (List<String>) r.get("items");
                List<Item> roomItems = new ArrayList<>();

                if (itemIds != null) {
                    for (String itemId : itemIds) {
                        roomItems.add(allItems.get(itemId));
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

                rooms.put(id, new Room(id, desc, exits, roomItems, roomEnemies));
            }

            return rooms;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}