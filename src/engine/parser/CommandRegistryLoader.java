package engine.parser;

import engine.commands.InterfaceCommand;
import engine.loader.CommandDefinition;

import java.util.List;

public class CommandRegistryLoader {

    public static void registerCommands(CommandCutter parser, List<CommandDefinition> commandDefinitions) {
        if (commandDefinitions == null || commandDefinitions.isEmpty()) {
            System.out.println("No commands were defined in gameConfig.");
            return;
        }

        for (CommandDefinition definition : commandDefinitions) {
            registerCommand(parser, definition);
        }
    }

    private static void registerCommand(CommandCutter parser, CommandDefinition definition) {
        if (definition == null || definition.getClassName() == null || definition.getClassName().isBlank()) {
            System.out.println("Skipping command with missing className.");
            return;
        }

        if (definition.getAliases() == null || definition.getAliases().isEmpty()) {
            System.out.println("Skipping command " + definition.getClassName() + ": no aliases defined.");
            return;
        }

        try {
            Class<?> commandClass = Class.forName(definition.getClassName());
            Object instance = commandClass.getDeclaredConstructor().newInstance();

            if (!(instance instanceof InterfaceCommand)) {
                System.out.println("Command class does not implement InterfaceCommand: " + definition.getClassName());
                return;
            }

            parser.registerCommand(
                    (InterfaceCommand) instance,
                    definition.getAliases().toArray(new String[0])
            );

        } catch (ClassNotFoundException e) {
            System.out.println("Command class not found: " + definition.getClassName());
        } catch (ReflectiveOperationException e) {
            System.out.println("Could not create command " + definition.getClassName() + ": " + e.getMessage());
        }
    }
}
