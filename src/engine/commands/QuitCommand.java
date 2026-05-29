package engine.commands;

import engine.core.CurrentGameState;

import java.util.List;

public class QuitCommand implements InterfaceCommand, NonReplayableCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        System.out.println("Thanks for playing!");
        System.exit(0);
    }
}
