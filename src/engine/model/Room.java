package engine.model;

import java.util.List;
import java.util.Map;

public class Room {

    private final String id;
    private final String description;
    private Map<String, String> exits;
    private List<Item> items;

    public Room(String id, String description, Map<String, String> exits, List<Item> items) {
        this.id = id;
        this.description = description;
        this.exits = exits;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getExits() {
        return exits;
    }

    public List<Item> getItems() {
        return items;
    }

    public String getExit(String direction) {
        return exits.get(direction);
    }
}