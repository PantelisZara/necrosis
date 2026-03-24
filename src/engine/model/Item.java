package engine.model;

public class Item {

    private String id;
    private String name;
    private String description;
    private boolean portable;

    public Item(String id, String name, String description, boolean portable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.portable = portable;
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

    public boolean isPortable() {
        return portable;
    }
}