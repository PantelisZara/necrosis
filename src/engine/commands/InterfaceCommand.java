package engine.commands;

import engine.core.CurrentGameState;
import java.util.List;

public interface InterfaceCommand {
    void execute(CurrentGameState gameState, List<String> args);
}