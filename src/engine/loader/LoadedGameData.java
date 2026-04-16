package engine.loader;

import engine.model.Room;
import engine.model.ZaunPhase;

import java.util.List;
import java.util.Map;

public class LoadedGameData {

    private final Map<String, Room> rooms;
    private final List<ZaunPhase> zaunPhases;

    public LoadedGameData(Map<String, Room> rooms, List<ZaunPhase> zaunPhases) {
        this.rooms = rooms;
        this.zaunPhases = zaunPhases;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public List<ZaunPhase> getZaunPhases() {
        return zaunPhases;
    }
}