package engine.loader;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CommandDefinition {

    private String className;
    private List<String> aliases;
    private JsonObject config;

    public CommandDefinition() {
        this.aliases = new ArrayList<>();
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

    public JsonObject getConfig() {
        return config;
    }
}
