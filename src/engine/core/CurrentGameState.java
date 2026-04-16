package engine.core;

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
    private int zaunPhase;
    private List<ZaunPhase> zaunPhases;

    public CurrentGameState(Map<String, Room> rooms, Player player) {
        this.rooms = rooms;
        this.player = player;
        this.flags = new HashMap<>();
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
}