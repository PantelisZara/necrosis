package engine.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import engine.core.CurrentGameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommand implements InterfaceCommand, ConfigurableCommand, NonReplayableCommand {

    private static final Gson GSON = new Gson();

    private String title = "Available commands:";
    private List<String> usages = defaultUsages();

    @Override
    public void configure(JsonObject config) {
        if (config == null) {
            return;
        }

        HelpConfig parsedConfig = GSON.fromJson(config, HelpConfig.class);
        if (parsedConfig == null) {
            return;
        }

        if (parsedConfig.title != null && !parsedConfig.title.isBlank()) {
            title = parsedConfig.title;
        }

        if (parsedConfig.usages != null && !parsedConfig.usages.isEmpty()) {
            usages = parsedConfig.usages;
        }
    }

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        System.out.println(title);
        for (String usage : usages) {
            System.out.println("- " + usage);
        }
    }

    private static List<String> defaultUsages() {
        return new ArrayList<>(Arrays.asList(
                "look",
                "look at <thing>",
                "take <item>",
                "read <item>",
                "use <thing>",
                "go <direction>",
                "inventory",
                "save",
                "load",
                "history",
                "quit"
        ));
    }

    private static class HelpConfig {
        private String title;
        private List<String> usages;
    }
}
