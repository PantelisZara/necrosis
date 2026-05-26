package engine.loader;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {

    private String title;
    private String startRoomId;
    private List<String> endingFlags;
    private List<CommandDefinition> commands;

    public GameConfig() {
        this("", "", new ArrayList<>(), new ArrayList<>());
    }

    public GameConfig(String title, String startRoomId, List<String> endingFlags, List<CommandDefinition> commands) {
        this.title = title;
        this.startRoomId = startRoomId;
        this.endingFlags = endingFlags != null ? endingFlags : new ArrayList<>();
        this.commands = commands != null ? commands : new ArrayList<>();
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public String getStartRoomId() {
        return startRoomId != null ? startRoomId : "";
    }

    public List<String> getEndingFlags() {
        if (endingFlags == null) {
            endingFlags = new ArrayList<>();
        }

        return endingFlags;
    }

    public List<CommandDefinition> getCommands() {
        if (commands == null) {
            commands = new ArrayList<>();
        }

        return commands;
    }
}
