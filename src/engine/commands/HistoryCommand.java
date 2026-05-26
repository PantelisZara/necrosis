package engine.commands;

import engine.core.CurrentGameState;

import java.util.List;

public class HistoryCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        List<String> history = gameState.getCommandHistory();

        if (history.isEmpty()) {
            System.out.println("No command history yet.");
            return;
        }

        System.out.println("Command history:");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }
}
