package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Room;
import java.util.List;

public class GoCommand implements InterfaceCommand {
    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Go where?");
            return;
        }

        String direction = args.getFirst();
        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        String nextRoomId = currentRoom.getExit(direction);

        if (nextRoomId != null) {
            Room nextRoom = gameState.getRoom(nextRoomId);
            gameState.getPlayer().setCurrentRoom(nextRoom);
            System.out.println("You moved to: " + nextRoom.getDescription());


        } else {
            System.out.println("You cant go that way");
        }

    }
}
