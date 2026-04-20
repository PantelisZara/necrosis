package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Interactable;
import engine.model.Room;
import engine.model.Player;
import engine.model.Item;

import java.util.List;

public class UseCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Use what?");
            return;
        }

        String target = String.join(" ", args).toLowerCase();

        if (target.equals("bandage") || target.equals("bandages")) {
            useBandages(gameState);
            return;
        }

        if (engine.systems.ThreatSystem.triggerEnemyAttack(gameState, "use " + target)) {
            return;
        }

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


    }

    private void useBandages(CurrentGameState gameState) {
        Player player = gameState.getPlayer();

        if (!player.isInjured()) {
            System.out.println("You don't need bandages right now.");
            return;
        }

        Item bandages = player.findItemById("bandages");

        if (bandages == null) {
            System.out.println("You don't have any bandages.");
            return;
        }

        player.removeItem(bandages);
        player.heal();

        System.out.println("You quickly wrap your wounds with the bandages.");
        System.out.println("You are no longer bleeding.");
    }

}