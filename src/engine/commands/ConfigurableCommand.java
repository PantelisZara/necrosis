package engine.commands;

import com.google.gson.JsonObject;

public interface ConfigurableCommand {
    void configure(JsonObject config);
}
