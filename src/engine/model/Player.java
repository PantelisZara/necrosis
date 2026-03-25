package engine.model;

import java.util.List;

public class Player {

    private Room currentRoom;
    private List<Item> inventory;

    public Player(Room currentRoom, List<Item> inventory) {
        this.currentRoom = currentRoom;
        this.inventory = inventory;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<Item> getInventory() {
        return inventory;
    }
}