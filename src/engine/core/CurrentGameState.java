package engine.core;

import engine.model.Player;
import engine.model.Room;
import java.util.Map;

public class CurrentGameState {

    private Map<String, Room> rooms;
    private Player player;

    public CurrentGameState(Map<String, Room> rooms, Player player) {
        this.rooms = rooms;
        this.player = player;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Player getPlayer() {
        return player;
    }
}