package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Enemy;
import engine.model.EnemyType;
import engine.model.Room;
import engine.systems.ZaunEncounterSystem;

import java.util.ArrayList;
import java.util.List;

public class StrikeCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        List<Enemy> activeEnemies = getActiveEnemies(currentRoom);

        if (activeEnemies.isEmpty()) {
            System.out.println("There is nothing here to strike.");
            return;
        }

        Enemy targetEnemy = resolveTarget(activeEnemies, args, "ripper");

        if (targetEnemy == null) {
            return;
        }

        if (targetEnemy.getType() != EnemyType.RIPPER) {
            System.out.println("Striking that won't help much.");
            return;
        }

        if (gameState.getPlayer().findItemById("scrap_metal") == null) {
            System.out.println("You need something heavy to strike the ripper with.");
            return;
        }

        targetEnemy.defeat();
        System.out.println("You swing the scrap metal with all your strength.");
        System.out.println("The ripper collapses with a final shriek.");
        ZaunEncounterSystem.advanceIfNeeded(gameState);
    }

    private List<Enemy> getActiveEnemies(Room room) {
        List<Enemy> activeEnemies = new ArrayList<>();
        for (Enemy enemy : room.getEnemies()) {
            if (!enemy.isDefeated()) {
                activeEnemies.add(enemy);
            }
        }
        return activeEnemies;
    }

    private Enemy resolveTarget(List<Enemy> enemies, List<String> args, String defaultName) {
        if (args == null || args.isEmpty()) {
            for (Enemy enemy : enemies) {
                if (enemy.getName().equalsIgnoreCase(defaultName)) {
                    return enemy;
                }
            }
            System.out.println("Strike what?");
            return null;
        }

        String targetName = String.join(" ", args).toLowerCase();
        for (Enemy enemy : enemies) {
            String enemyName = enemy.getName().toLowerCase();
            if (enemyName.equals(targetName) || (enemyName + "s").equals(targetName)) {
                return enemy;
            }
        }

        System.out.println("There is no active enemy here by that name.");
        return null;
    }
}