package engine.commands;

import engine.core.CurrentGameState;
import engine.save.SaveManager;

import java.io.IOException;
import java.util.List;

public class LoadCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (!SaveManager.saveExists()) {
            System.out.println("No saved game found yet.");
            return;
        }

        try {
            SaveManager.load(gameState);
            System.out.println("Game loaded successfully from " + SaveManager.getSavePath() + ".");
        } catch (IOException | IllegalStateException e) {
            System.out.println("Could not load the saved game: " + e.getMessage());
        }
    }
}
