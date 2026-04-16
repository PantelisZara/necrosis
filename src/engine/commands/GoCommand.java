package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Exit;
import engine.model.Room;

import java.util.List;

public class GoCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Go where?");
            return;
        }

        if (engine.systems.ThreatSystem.triggerEnemyAttack(gameState, "move")) {
            return;
        }

        String direction = args.get(0).toLowerCase();

        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        Exit exit = currentRoom.getExit(direction);

        if (exit == null) {
            System.out.println("You can't go that way.");
            return;
        }

        if (exit.isLocked()) {
            System.out.println("That way is locked.");
            return;
        }

        Room nextRoom = gameState.getRoom(exit.getTargetRoomId());

        if (nextRoom == null) {
            System.out.println("The destination room could not be found.");
            return;
        }

        gameState.getPlayer().setCurrentRoom(nextRoom);
        System.out.println("You moved to: " + nextRoom.getDescription());
    }
}