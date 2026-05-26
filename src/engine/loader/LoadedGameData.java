package engine.loader;

import engine.model.Item;
import engine.model.Room;
import engine.model.ZaunPhase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadedGameData {

    private final Map<String, Room> rooms;
    private final List<ZaunPhase> zaunPhases;
    private final List<String> introLines;
    private final GameConfig gameConfig;
    private final Map<String, Item> itemTemplates;

    public LoadedGameData(
            Map<String, Room> rooms,
            List<ZaunPhase> zaunPhases,
            List<String> introLines,
            GameConfig gameConfig,
            Map<String, Item> itemTemplates
    ) {
        this.rooms = rooms;
        this.zaunPhases = zaunPhases;
        this.introLines = introLines;
        this.gameConfig = gameConfig;
        this.itemTemplates = itemTemplates;
    }

    public static LoadedGameData empty() {
        return new LoadedGameData(
                new HashMap<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new GameConfig(),
                new HashMap<>()
        );
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public List<ZaunPhase> getZaunPhases() {
        return zaunPhases;
    }

    public List<String> getIntroLines() {
        return introLines;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public String getTitle() {
        return gameConfig.getTitle();
    }

    public String getStartRoomId() {
        return gameConfig.getStartRoomId();
    }

    public List<String> getEndingFlags() {
        return gameConfig.getEndingFlags();
    }

    public List<CommandDefinition> getCommandDefinitions() {
        return gameConfig.getCommands();
    }

    public Map<String, Item> getItemTemplates() {
        return itemTemplates;
    }
}
