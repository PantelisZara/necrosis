package engine.parser;

import engine.commands.InterfaceCommand;
import engine.core.CurrentGameState;

import java.util.*;

public class CommandCutter {

    private final Map<String, InterfaceCommand> commands = new HashMap<>();

    public void registerCommand(InterfaceCommand command, String... synonyms) {
        for (String alias : synonyms) {
            if (alias != null && !alias.isBlank()) {
                commands.put(alias.toLowerCase(), command);
            }
        }
    }

    public boolean parse_Execute(String input, CurrentGameState gameState) {
        input = input.trim();

        if (input.isEmpty()) {
            System.out.println("No command entered.");
            return false;
        }

        List<String> tokens = Arrays.asList(input.split("\\s+"));
        CommandMatch match = findLongestCommandMatch(tokens);

        if (match == null) {
            System.out.println("Unknown command.");
            return false;
        }

        List<String> args = tokens.subList(match.wordCount, tokens.size());
        match.command.execute(gameState, args);
        return true;
    }

    private CommandMatch findLongestCommandMatch(List<String> tokens) {
        for (int wordCount = tokens.size(); wordCount >= 1; wordCount--) {
            String alias = String.join(" ", tokens.subList(0, wordCount)).toLowerCase();
            InterfaceCommand command = commands.get(alias);

            if (command != null) {
                return new CommandMatch(command, wordCount);
            }
        }

        return null;
    }

    private static class CommandMatch {
        private final InterfaceCommand command;
        private final int wordCount;

        private CommandMatch(InterfaceCommand command, int wordCount) {
            this.command = command;
            this.wordCount = wordCount;
        }
    }
}
