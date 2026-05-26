package engine.commands;

import engine.core.CurrentGameState;
import engine.save.SaveManager;

import java.io.IOException;
import java.util.List;

public class SaveCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        try {
            SaveManager.save(gameState);
            System.out.println("Game saved successfully to " + SaveManager.getSavePath() + ".");
        } catch (IOException e) {
            System.out.println("Could not save the game: " + e.getMessage());
        }
    }
}
