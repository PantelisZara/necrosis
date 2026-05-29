package engine.model;

import java.util.List;
import java.util.Map;

public class Room {

    private final String id;
    private final String description;
    private final Map<String, Exit> exits;
    private final List<Item> items;
    private final List<Enemy> enemies;
    private final List<Interactable> interactables;
    private final List<Npc> npcs;
    private final List<String> hints;

    public Room(
            String id,
            String description,
            Map<String, Exit> exits,
            List<Item> items,
            List<Enemy> enemies,
            List<Interactable> interactables,
            List<Npc> npcs,
            List<String> hints
    ) {
        this.id = id;
        this.description = description;
        this.exits = exits;
        this.items = items;
        this.enemies = enemies;
        this.interactables = interactables;
        this.npcs = npcs;
        this.hints = hints;
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

    public Interactable findInteractableByName(String name) {
        for (Interactable interactable : interactables) {
            if (matches(name, interactable.getId()) || matches(name, interactable.getName())) {
                return interactable;
            }
        }
        return null;
    }

    public List<Interactable> getInteractables() {
        return interactables;
    }

    public List<Npc> getNpcs() {
        return npcs;
    }

    public Npc findNpcByName(String name) {
        for (Npc npc : npcs) {
            if (matches(name, npc.getId()) || matches(name, npc.getName())) {
                return npc;
            }
        }
        return null;
    }

    public List<String> getHints() {
        return hints;
    }

    private boolean matches(String input, String value) {
        if (input == null || value == null) {
            return false;
        }

        String normalizedInput = input.toLowerCase().trim();
        String normalizedValue = value.toLowerCase().replace("_", " ").trim();
        return normalizedInput.equals(normalizedValue);
    }
}
