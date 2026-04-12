package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Room;

import java.util.List;

public class EnterCommand implements InterfaceCommand {

    private static final String TERMINAL_CODE = "7314";

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.size() < 2) {
            System.out.println("Usage: enter code <number>");
            return;
        }

        String firstArg = args.get(0).toLowerCase();

        if (!firstArg.equals("code")) {
            System.out.println("Usage: enter code <number>");
            return;
        }

        String enteredCode = String.join(" ", args.subList(1, args.size())).trim();
        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        if (!currentRoom.getId().equalsIgnoreCase("research_room")) {
            System.out.println("There is nothing here that accepts a code.");
            return;
        }

        if (!gameState.isFlagTrue("power_restored")) {
            System.out.println("The terminal has no power.");
            return;
        }

        if (gameState.isFlagTrue("terminal_unlocked")) {
            System.out.println("The terminal is already unlocked.");
            return;
        }

        if (!gameState.isFlagTrue("terminal_prompt_active")) {
            System.out.println("You need to activate the terminal first.");
            return;
        }

        if (enteredCode.equals(TERMINAL_CODE)) {
            gameState.setFlag("terminal_unlocked", true);
            gameState.setFlag("terminal_prompt_active", false);

            System.out.println("Code accepted.");
            System.out.println("Research archives unlocked.");
            System.out.println("A hidden compartment opens somewhere in the room.");
        } else {
            System.out.println("Access denied.");
        }
    }
}