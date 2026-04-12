package engine.core;

import engine.model.Player;
import engine.model.Room;

import java.util.HashMap;
import java.util.Map;

public class CurrentGameState {

    private final Map<String, Room> rooms;
    private final Player player;
    private final Map<String, Boolean> flags;

    public CurrentGameState(Map<String, Room> rooms, Player player) {
        this.rooms = rooms;
        this.player = player;
        this.flags = new HashMap<>();
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Player getPlayer() {
        return player;
    }

    public Room getRoom(String id) {
        return rooms.get(id);
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
}