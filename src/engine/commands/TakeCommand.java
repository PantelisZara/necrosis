package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Item;
import engine.model.Room;

import java.util.ArrayList;
import java.util.List;

public class TakeCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Take what?");
            return;
        }

        String target = String.join(" ", args).toLowerCase();
        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        List<Item> roomItems = currentRoom.getItems();

        if (target.equals("all")) {
            takeAllItems(gameState, currentRoom, roomItems);
            return;
        }

        Item foundItem = null;

        for (Item item : roomItems) {
            if (item != null && item.getName().toLowerCase().equals(target)) {
                foundItem = item;
                break;
            }
        }

        if (foundItem == null) {
            System.out.println("There is no such item here.");
            return;
        }

        if (!foundItem.isPortable()) {
            System.out.println("You cannot take that.");
            return;
        }

        currentRoom.removeItem(foundItem);
        gameState.getPlayer().addItem(foundItem);

        System.out.println("You took the " + foundItem.getName() + ".");
    }

    private void takeAllItems(CurrentGameState gameState, Room currentRoom, List<Item> roomItems) {
        if (roomItems == null || roomItems.isEmpty()) {
            System.out.println("There is nothing here to take.");
            return;
        }

        List<Item> itemsToTake = new ArrayList<>();

        for (Item item : roomItems) {
            if (item != null && item.isPortable()) {
                itemsToTake.add(item);
            }
        }

        if (itemsToTake.isEmpty()) {
            System.out.println("There is nothing portable here.");
            return;
        }

        System.out.println("You took:");

        for (Item item : itemsToTake) {
            currentRoom.removeItem(item);
            gameState.getPlayer().addItem(item);
            System.out.println("- " + item.getName());
        }
    }
}