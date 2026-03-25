package engine.parser;
import engine.commands.InterfaceCommand;
import engine.core.CurrentGameState;
import java.util.*;

public class CommandCutter {
    private Map<String, InterfaceCommand> commands = new HashMap<>();
    public void registerCommand(String keyword, InterfaceCommand command) {
        commands.put(keyword, command);
    }

    public void parseAndExecute(String input, CurrentGameState gameState) {

        List<String> tokens = Arrays.asList(input.split(" "));

        if (tokens.isEmpty()) {
            System.out.println("No command entered.");
            return;
        }

        String commandWord = tokens.getFirst();

        InterfaceCommand command = commands.get(commandWord);

        if (command == null) {
            System.out.println("Unknown command.");
            return;
        }

        List<String> args = tokens.subList(1, tokens.size());

        command.execute(gameState, args);
    }
}
