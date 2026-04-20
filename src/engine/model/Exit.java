package engine.model;

public class Exit {

    private final String targetRoomId;
    private boolean locked;

    public Exit(String targetRoomId, boolean locked) {
        this.targetRoomId = targetRoomId;
        this.locked = locked;
    }

    public String getTargetRoomId() {
        return targetRoomId;
    }

    public boolean isLocked() {
        return locked;
    }

    public void unlock() {
        this.locked = false;
    }
}