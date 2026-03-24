package engine.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private Room currentRoom;
    private List<Item> inventory = new ArrayList<>();

    public Player(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void move(Room room) {
        currentRoom = room;
    }

    public List<Item> getInventory() {
        return inventory;
    }
}