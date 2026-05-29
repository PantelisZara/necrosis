package engine.parser;

import engine.commands.InterfaceCommand;
import engine.core.CurrentGameState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandCutter {

    private final Map<String, InterfaceCommand> commands = new HashMap<>();

    public void registerCommand(InterfaceCommand command, String... synonyms) {
        for (String alias : synonyms) {
            if (alias != null && !alias.isBlank()) {
                String normalizedAlias = alias.trim().toLowerCase();
                if (commands.containsKey(normalizedAlias)) {
                    throw new IllegalArgumentException("Duplicate command alias: " + normalizedAlias);
                }

                commands.put(normalizedAlias, command);
            }
        }
    }

    public CommandExecutionResult parseAndExecute(String input, CurrentGameState gameState) {
        CommandMatch match = findCommandMatch(input, true);

        if (match == null) {
            return CommandExecutionResult.notExecuted();
        }

        match.command.execute(gameState, match.args);
        return CommandExecutionResult.executed(match.command);
    }

    public boolean isReplayableCommand(String input) {
        CommandMatch match = findCommandMatch(input, false);
        return match != null && match.isReplayable();
    }

    public boolean isKnownCommand(String input) {
        return findCommandMatch(input, false) != null;
    }

    private CommandMatch findCommandMatch(String input, boolean printErrors) {
        if (input == null) {
            input = "";
        }

        input = input.trim();

        if (input.isEmpty()) {
            if (printErrors) {
                System.out.println("No command entered.");
            }
            return null;
        }

        List<String> tokens = Arrays.asList(input.split("\\s+"));
        CommandMatch match = findLongestCommandMatch(tokens);

        if (match == null) {
            if (printErrors) {
                System.out.println("Unknown command.");
            }
            return null;
        }

        return new CommandMatch(match.command, match.wordCount, tokens.subList(match.wordCount, tokens.size()));
    }

    private CommandMatch findLongestCommandMatch(List<String> tokens) {
        for (int wordCount = tokens.size(); wordCount >= 1; wordCount--) {
            String alias = String.join(" ", tokens.subList(0, wordCount)).toLowerCase();
            InterfaceCommand command = commands.get(alias);

            if (command != null) {
                return new CommandMatch(command, wordCount, List.of());
            }
        }

        return null;
    }

    private static class CommandMatch {
        private final InterfaceCommand command;
        private final int wordCount;
        private final List<String> args;

        private CommandMatch(InterfaceCommand command, int wordCount, List<String> args) {
            this.command = command;
            this.wordCount = wordCount;
            this.args = args;
        }

        private boolean isReplayable() {
            return !(command instanceof engine.commands.NonReplayableCommand);
        }
    }
}
