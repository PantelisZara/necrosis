package engine.parser;

import engine.commands.InterfaceCommand;
import engine.core.CurrentGameState;

import java.util.*;

public class CommandCutter {

    private Map<String, InterfaceCommand> commands = new HashMap<>();

    public void registerCommand(InterfaceCommand command, String... synonyms) {
        for (String alias : synonyms) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    public void parse_Execute(String input, CurrentGameState gameState) {
        input = input.trim();

        if (input.isEmpty()) {
            System.out.println("No command entered.");
            return;
        }

        List<String> tokens = Arrays.asList(input.split("\\s+"));

        String commandKey = null;
        List<String> args;

        if (tokens.size() >= 2) {
            String twoWordCommand = (tokens.get(0) + " " + tokens.get(1)).toLowerCase();

            if (commands.containsKey(twoWordCommand)) {
                commandKey = twoWordCommand;
                args = tokens.subList(2, tokens.size());
                InterfaceCommand command = commands.get(commandKey);
                command.execute(gameState, args);
                return;
            }
        }

        String oneWordCommand = tokens.get(0).toLowerCase();
        InterfaceCommand command = commands.get(oneWordCommand);

        if (command == null) {
            System.out.println("Unknown command.");
            return;
        }

        args = tokens.subList(1, tokens.size());
        command.execute(gameState, args);
    }
}