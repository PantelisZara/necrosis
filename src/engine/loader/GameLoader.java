package engine.loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import engine.model.Room;
import engine.model.Item;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

public class GameLoader {

    public static Map<String, Room> loadRooms(String filePath) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(filePath);

            // Ορίζουμε τύπο για rooms
            Type type = new TypeToken<Map<String, List<Map<String, Object>>>>(){}.getType();
            Map<String, List<Map<String, Object>>> data = gson.fromJson(reader, type);

            Map<String, Room> rooms = new HashMap<>();
            List<Map<String, Object>> roomList = data.get("rooms");
            List<Map<String, Object>> itemList = data.get("items");

            // Δημιουργούμε αντικείμενα Item
            Map<String, Item> allItems = new HashMap<>();
            for (Map<String, Object> i : itemList) {
                String id = (String) i.get("id");
                String name = (String) i.get("name");
                String desc = (String) i.get("description");
                boolean is_prtbl = true;
                allItems.put(id, new Item(id, name, desc, is_prtbl));
            }

            // Δημιουργούμε δωμάτια
            for (Map<String, Object> r : roomList) {
                String id = (String) r.get("id");
                String desc = (String) r.get("description");
                Map<String, String> exits = (Map<String, String>) r.get("exits");

                List<String> itemIds = (List<String>) r.get("items");
                List<Item> roomItems = new ArrayList<>();
                if (itemIds != null) {
                    for (String itemId : itemIds) {
                        roomItems.add(allItems.get(itemId));
                    }
                }

                rooms.put(id, new Room(id, desc, exits, roomItems));
            }

            return rooms;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}