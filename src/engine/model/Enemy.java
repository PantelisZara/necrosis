package engine.model;

public class Enemy {

    private final String id;
    private final String name;
    private final String description;
    private final EnemyType type;
    private boolean defeated;

    public Enemy(String id, String name, String description, EnemyType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.defeated = false;
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

    public EnemyType getType() {
        return type;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public void defeat() {
        this.defeated = true;
    }
}