package engine.core;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {

    private final List<String> commands;

    public CommandHistory() {
        this.commands = new ArrayList<>();
    }

    public void record(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        commands.add(input.trim());
    }

    public List<String> getCommands() {
        return new ArrayList<>(commands);
    }

    public boolean isEmpty() {
        return commands.isEmpty();
    }

    public void replaceWith(List<String> savedCommands) {
        commands.clear();

        if (savedCommands == null) {
            return;
        }

        for (String command : savedCommands) {
            record(command);
        }
    }
}
