package engine.core;

import engine.model.EncounterPhase;
import engine.model.Item;
import engine.model.Player;
import engine.model.Room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentGameState {

    private final Map<String, Room> rooms;
    private final Player player;
    private final Map<String, Boolean> flags;
    private final Map<String, Item> itemTemplates;
    private final CommandHistory commandHistory;
    private GameSessionController gameSessionController;
    private String gameDataPath;
    private boolean replaying;
    private int encounterPhase;
    private List<EncounterPhase> encounterPhases;

    public CurrentGameState(Map<String, Room> rooms, Player player) {
        this(rooms, player, new HashMap<>());
    }

    public CurrentGameState(Map<String, Room> rooms, Player player, Map<String, Item> itemTemplates) {
        this.rooms = rooms;
        this.player = player;
        this.flags = new HashMap<>();
        this.itemTemplates = itemTemplates != null ? itemTemplates : new HashMap<>();
        this.commandHistory = new CommandHistory();
        this.replaying = false;
        this.encounterPhase = 0;
        this.encounterPhases = new ArrayList<>();
    }

    public Player getPlayer() {
        return player;
    }

    public int getEncounterPhase() {
        return encounterPhase;
    }

    public void setEncounterPhase(int encounterPhase) {
        this.encounterPhase = encounterPhase;
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public List<EncounterPhase> getEncounterPhases() {
        return encounterPhases;
    }

    public void setEncounterPhases(List<EncounterPhase> encounterPhases) {
        this.encounterPhases = encounterPhases != null ? encounterPhases : new ArrayList<>();
    }

    public void setFlag(String flagName, boolean value) {
        flags.put(flagName, value);
    }

    public boolean isFlagTrue(String flagName) {
        return flags.getOrDefault(flagName, false);
    }

    public Map<String, Boolean> getFlags() {
        return flags;
    }

    public Item createItemById(String itemId) {
        Item template = itemTemplates.get(itemId);

        if (template == null) {
            return null;
        }

        return new Item(
                template.getId(),
                template.getName(),
                template.getDescription(),
                template.isPortable()
        );
    }

    public void recordCommand(String input) {
        if (replaying) {
            return;
        }

        commandHistory.record(input);
    }

    public List<String> getCommandHistory() {
        return commandHistory.getCommands();
    }

    public void restoreCommandHistory(List<String> savedCommands) {
        commandHistory.replaceWith(savedCommands);
    }

    public void setGameSessionController(GameSessionController gameSessionController) {
        this.gameSessionController = gameSessionController;
    }

    public void loadSavedGame() throws IOException {
        if (gameSessionController == null) {
            throw new IllegalStateException("No game session is available to load saved games.");
        }

        gameSessionController.loadSavedGame();
    }

    public String getGameDataPath() {
        return gameDataPath;
    }

    public void setGameDataPath(String gameDataPath) {
        this.gameDataPath = gameDataPath;
    }

    public void beginReplay() {
        this.replaying = true;
    }

    public void endReplay() {
        this.replaying = false;
    }
}
