package engine.core;

import engine.model.Item;
import engine.model.Player;
import engine.model.Room;
import engine.model.ZaunPhase;

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
    private int zaunPhase;
    private List<ZaunPhase> zaunPhases;

    public CurrentGameState(Map<String, Room> rooms, Player player) {
        this(rooms, player, new HashMap<>());
    }

    public CurrentGameState(Map<String, Room> rooms, Player player, Map<String, Item> itemTemplates) {
        this.rooms = rooms;
        this.player = player;
        this.flags = new HashMap<>();
        this.itemTemplates = itemTemplates != null ? itemTemplates : new HashMap<>();
        this.commandHistory = new CommandHistory();
        this.zaunPhase = 0;
        this.zaunPhases = new ArrayList<>();
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Player getPlayer() {
        return player;
    }

    public int getZaunPhase() {
        return zaunPhase;
    }

    public void setZaunPhase(int zaunPhase) {
        this.zaunPhase = zaunPhase;
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public List<ZaunPhase> getZaunPhases() {
        return zaunPhases;
    }

    public void setZaunPhases(List<ZaunPhase> zaunPhases) {
        this.zaunPhases = zaunPhases;
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
        commandHistory.record(input);
    }

    public List<String> getCommandHistory() {
        return commandHistory.getCommands();
    }

    public boolean hasCommandHistory() {
        return !commandHistory.isEmpty();
    }

    public void restoreCommandHistory(List<String> savedCommands) {
        commandHistory.replaceWith(savedCommands);
    }
}
