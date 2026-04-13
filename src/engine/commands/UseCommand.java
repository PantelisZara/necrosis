package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Exit;
import engine.model.Interactable;
import engine.model.Room;

import java.util.List;

public class UseCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Use what?");
            return;
        }

        String target = String.join(" ", args).toLowerCase();
        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        Interactable interactable = currentRoom.findInteractableByName(target);

        if (interactable == null) {
            System.out.println("You can't use that here.");
            return;
        }

        if (interactable.getRequiredFlag() != null &&
                !gameState.isFlagTrue(interactable.getRequiredFlag())) {
            System.out.println(interactable.getFailureMessage());
            return;
        }

        if (interactable.getRequiredItemId() != null &&
                gameState.getPlayer().findItemById(interactable.getRequiredItemId()) == null) {
            System.out.println(interactable.getFailureMessage());
            return;
        }

        if (interactable.getRequiredItemId() != null) {
            var item = gameState.getPlayer().findItemById(interactable.getRequiredItemId());
            if (item != null) {
                gameState.getPlayer().removeItem(item);
            }
        }

        if (interactable.getSetsFlag() != null) {
            gameState.setFlag(interactable.getSetsFlag(), true);
        }

        System.out.println(interactable.getSuccessMessage());

        if ("power_restored".equals(interactable.getSetsFlag())) {
            Room lobby = gameState.getRoom("lobby");
            if (lobby != null) {
                Exit northExit = lobby.getExit("north");
                if (northExit != null) {
                    northExit.unlock();
                }
            }
            gameState.setFlag("boss_room_unlocked", true);
            System.out.println("You hear security doors unlocking somewhere in the facility.");
        }
    }
}