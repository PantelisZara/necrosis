package engine.model;

public class EnemySpawn {

    private final String type;
    private final int count;

    public EnemySpawn(String type, int count) {
        this.type = type;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public int getCount() {
        return count;
    }
}