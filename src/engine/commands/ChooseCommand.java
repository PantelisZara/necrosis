package engine.commands;

import engine.core.CurrentGameState;
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
        gameState.setFlag("ending_survival", true);
        gameState.setFlag("final_choice_available", false);

        System.out.println("You strike Dr. Zaun down before he can say another word.");
        System.out.println("His keycard falls from his hand as he collapses.");
        System.out.println("Emily meets you at the escape route, shaken but alive.");
        System.out.println("ENDING: SURVIVAL");
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