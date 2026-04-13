package engine.model;

import java.util.List;
import java.util.Map;

public class Room {

    private final String id;
    private final String description;
    private final Map<String, Exit> exits;
    private final List<Item> items;
    private final List<Enemy> enemies;

    public Room(String id, String description, Map<String, Exit> exits, List<Item> items, List<Enemy> enemies) {
        this.id = id;
        this.description = description;
        this.exits = exits;
        this.items = items;
        this.enemies = enemies;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Exit> getExits() {
        return exits;
    }

    public List<Item> getItems() {
        return items;
    }

    public Exit getExit(String direction) {
        return exits.get(direction);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public boolean hasActiveEnemies() {
        for (Enemy enemy : enemies) {
            if (!enemy.isDefeated()) {
                return true;
            }
        }
        return false;
    }

    public Enemy findActiveEnemyByName(String enemyName) {
        for (Enemy enemy : enemies) {
            if (!enemy.isDefeated() && enemy.getName().toLowerCase().equals(enemyName.toLowerCase())) {
                return enemy;
            }
        }
        return null;
    }
}