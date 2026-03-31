package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Item;
import engine.model.Room;

import java.util.List;
import java.util.Map;

public class LookCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        System.out.println(currentRoom.getDescription());

        List<Item> items = currentRoom.getItems();
        if (items == null || items.isEmpty()) {
            System.out.println("There are no items here.");
        } else {
            System.out.println("You see:");
            for (Item item : items) {
                System.out.println("- " + item.getName());
            }
        }

        Map<String, String> exits = currentRoom.getExits();
        if (exits == null || exits.isEmpty()) {
            System.out.println("There are no exits.");
        } else {
            System.out.println("Exits:");
            for (String direction : exits.keySet()) {
                System.out.println("- " + direction);
            }
        }
    }
}