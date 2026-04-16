package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Exit;
import engine.model.Room;

import java.util.List;

public class EnterCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {

        if (!gameState.isFlagTrue("terminal_prompt_active")) {
            System.out.println("There is nothing to enter a code into.");
            return;
        }

        if (args.size() < 2) {
            System.out.println("Enter what code?");
            return;
        }

        String code = args.get(1);

        if (!code.equals("7314")) {
            System.out.println("ACCESS DENIED.");
            return;
        }

        System.out.println("ACCESS GRANTED.");
        System.out.println("Security door unlocking...");

        gameState.setFlag("terminal_unlocked", true);
        gameState.setFlag("terminal_prompt_active", false);

        Room lobby = gameState.getRoom("lobby");
        if (lobby != null) {
            Exit northExit = lobby.getExit("north");
            if (northExit != null) {
                northExit.unlock();
            }
        }
    }
}