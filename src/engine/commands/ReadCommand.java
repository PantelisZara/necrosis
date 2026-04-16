package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Item;
import engine.model.Room;

import java.util.List;

public class ReadCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Read what?");
            return;
        }
        if (engine.systems.ThreatSystem.triggerEnemyAttack(gameState, "read")) {
            return;
        }

        String targetName = String.join(" ", args).toLowerCase();

        for (Item item : gameState.getPlayer().getInventory()) {
            if (item != null && item.getName().toLowerCase().equals(targetName)) {
                System.out.println(item.getDescription());
                return;
            }
        }

        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        for (Item item : currentRoom.getItems()) {
            if (item != null && item.getName().toLowerCase().equals(targetName)) {
                System.out.println(item.getDescription());
                return;
            }
        }

        System.out.println("There is nothing like that to read.");
    }
}