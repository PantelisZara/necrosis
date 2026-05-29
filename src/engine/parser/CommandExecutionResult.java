package engine.parser;

import engine.commands.InterfaceCommand;
import engine.commands.NonReplayableCommand;

public class CommandExecutionResult {

    private final boolean executed;
    private final InterfaceCommand command;

    private CommandExecutionResult(boolean executed, InterfaceCommand command) {
        this.executed = executed;
        this.command = command;
    }

    public static CommandExecutionResult notExecuted() {
        return new CommandExecutionResult(false, null);
    }

    public static CommandExecutionResult executed(InterfaceCommand command) {
        return new CommandExecutionResult(true, command);
    }

    public boolean wasExecuted() {
        return executed;
    }

    public InterfaceCommand getCommand() {
        return command;
    }

    public boolean isReplayable() {
        return executed && !(command instanceof NonReplayableCommand);
    }
}
