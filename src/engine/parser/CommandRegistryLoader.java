package engine.parser;

import engine.commands.ConfigurableCommand;
import engine.commands.InterfaceCommand;
import engine.loader.CommandDefinition;

import java.util.List;

public class CommandRegistryLoader {

    public static void registerCommands(CommandCutter parser, List<CommandDefinition> commandDefinitions) {
        if (commandDefinitions == null || commandDefinitions.isEmpty()) {
            throw new IllegalStateException("No commands were defined in gameConfig.");
        }

        for (CommandDefinition definition : commandDefinitions) {
            registerCommand(parser, definition);
        }
    }

    private static void registerCommand(CommandCutter parser, CommandDefinition definition) {
        if (definition == null || definition.getClassName() == null || definition.getClassName().isBlank()) {
            throw new IllegalStateException("Command definition is missing className.");
        }

        if (definition.getAliases().isEmpty()) {
            throw new IllegalStateException("Command " + definition.getClassName() + " has no aliases defined.");
        }

        try {
            Class<?> commandClass = Class.forName(definition.getClassName());
            Object instance = commandClass.getDeclaredConstructor().newInstance();

            if (!(instance instanceof InterfaceCommand)) {
                throw new IllegalStateException(
                        "Command class does not implement InterfaceCommand: " + definition.getClassName()
                );
            }

            if (instance instanceof ConfigurableCommand configurableCommand) {
                configurableCommand.configure(definition.getConfig());
            }

            parser.registerCommand(
                    (InterfaceCommand) instance,
                    definition.getAliases().toArray(new String[0])
            );

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Command class not found: " + definition.getClassName(), e);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not create command "
                    + definition.getClassName() + ": " + e.getMessage(), e);
        }
    }
}
