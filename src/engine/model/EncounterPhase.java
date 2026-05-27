package engine.model;

import java.util.List;

public class EncounterPhase {

    private final int phase;
    private final String message;
    private final List<EnemySpawn> enemies;

    public EncounterPhase(int phase, String message, List<EnemySpawn> enemies) {
        this.phase = phase;
        this.message = message;
        this.enemies = enemies;
    }

    public int getPhase() {
        return phase;
    }

    public String getMessage() {
        return message;
    }

    public List<EnemySpawn> getEnemies() {
        return enemies;
    }
}
