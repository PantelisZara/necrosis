package engine.loader;

import java.util.ArrayList;
import java.util.List;

public class CommandDefinition {

    private String className;
    private List<String> aliases;

    public CommandDefinition() {
        this("", new ArrayList<>());
    }

    public CommandDefinition(String className, List<String> aliases) {
        this.className = className;
        this.aliases = aliases != null ? aliases : new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public List<String> getAliases() {
        if (aliases == null) {
            aliases = new ArrayList<>();
        }

        return aliases;
    }
}
