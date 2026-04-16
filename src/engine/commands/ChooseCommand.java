package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Exit;
import engine.model.Item;
import engine.model.Room;

import java.util.List;

public class ChooseCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Choose what?");
            return;
        }

        if (!gameState.isFlagTrue("final_choice_available")) {
            System.out.println("There is no major decision to make right now.");
            return;
        }

        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        if (!currentRoom.getId().equalsIgnoreCase("boss_room")) {
            System.out.println("This choice can only be made in front of Dr. Zaun.");
            return;
        }

        String choice = String.join(" ", args).toLowerCase().trim();

        switch (choice) {
            case "kill":
                chooseKill(gameState);
                break;

            case "spare":
                chooseSpare(gameState);
                break;

            case "join":
                chooseJoin(gameState);
                break;

            default:
                System.out.println("Available choices: kill, spare, join");
        }
    }

    private void chooseKill(CurrentGameState gameState) {
        gameState.setFlag("ending_kill", true);
        gameState.setFlag("final_choice_available", false);

        System.out.println("You strike Dr. Zaun down before he can say another word.");
        System.out.println("He collapses silently.");

        Item keycard = new Item(
                "keycard",
                "keycard",
                "A high-level security keycard bearing Dr. Zaun's clearance mark.",
                true
        );

        gameState.getPlayer().addItem(keycard);

        Room bossRoom = gameState.getRoom("boss_room");
        if (bossRoom != null) {
            Exit northExit = bossRoom.getExit("north");
            if (northExit != null) {
                northExit.unlock();
            }
        }

        System.out.println("You pick up Zaun's keycard.");
        System.out.println("The way to the escape route is now open.");
    }

    private void chooseSpare(CurrentGameState gameState) {
        gameState.setFlag("ending_cure", true);
        gameState.setFlag("final_choice_available", false);

        System.out.println("You lower your weapon and demand the full formula.");
        System.out.println("Zaun smiles faintly and reveals the Catalyst data.");
        System.out.println("With Emily's help, you begin working toward a cure.");
        System.out.println("ENDING: THE CURE");
    }

    private void chooseJoin(CurrentGameState gameState) {
        gameState.setFlag("ending_evolution", true);
        gameState.setFlag("final_choice_available", false);

        System.out.println("You step toward Zaun instead of away from him.");
        System.out.println("He nods, as if he expected this all along.");
        System.out.println("The facility is yours now. The Fallen will follow.");
        System.out.println("ENDING: EVOLUTION");
    }
}