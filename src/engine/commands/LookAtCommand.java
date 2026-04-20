package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Item;
import engine.model.Room;

import java.util.List;

public class LookAtCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Look at what?");
            return;
        }

        String targetName = String.join(" ", args).toLowerCase();
        Room currentRoom = gameState.getPlayer().getCurrentRoom();



        for (Item item : currentRoom.getItems()) {
            if (item.getName().toLowerCase().equals(targetName)) {
                System.out.println(item.getDescription());
                return;
            }
        }

        for (Item item : gameState.getPlayer().getInventory()) {
            if (item.getName().toLowerCase().equals(targetName)) {
                System.out.println(item.getDescription());
                return;
            }
        }

        System.out.println("You don't see that here.");
    }
}