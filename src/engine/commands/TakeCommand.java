package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Item;
import engine.model.Room;

import java.util.List;

public class TakeCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Take what?");
            return;
        }

        String itemName = String.join(" ", args).toLowerCase();

        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        List<Item> items = currentRoom.getItems();

        Item foundItem = null;

        for (Item item : items) {
            if (item.getName().toLowerCase().equals(itemName)) {
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
}