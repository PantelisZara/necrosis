package engine.model;

import java.util.List;

public class Player {

    private Room currentRoom;
    private List<Item> inventory;
    private boolean injured;
    private int turnsUntilDeath;
    private boolean alive;

    public Player(Room currentRoom, List<Item> inventory) {
        this.currentRoom = currentRoom;
        this.inventory = inventory;
        this.injured = false;
        this.turnsUntilDeath = 0;
        this.alive = true;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void removeItem(Item item) {inventory.remove(item); }

    public Item findItemById(String itemId) {
        for (Item item : inventory) {
            if (item.getId().equalsIgnoreCase(itemId)) {
                return item;
            }
        }
        return null;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public boolean isInjured() {
        return injured;
    }

    public int getTurnsUntilDeath() {
        return turnsUntilDeath;
    }

    public boolean isAlive() {
        return alive;
    }

    public void injure() {
        this.injured = true;
        this.turnsUntilDeath = 1;
    }

    public void decrementDeathCounter() {
        if (injured && turnsUntilDeath > 0) {
            turnsUntilDeath--;
            if (turnsUntilDeath == 0) {
                alive = false;
            }
        }
    }

    public void heal() {
        this.injured = false;
        this.turnsUntilDeath = 0;
    }
}