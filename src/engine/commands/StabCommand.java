package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Enemy;
import engine.model.EnemyType;
import engine.model.Room;
import engine.systems.ZaunEncounterSystem;

import java.util.ArrayList;
import java.util.List;

public class StabCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        List<Enemy> activeEnemies = getActiveEnemies(currentRoom);

        if (activeEnemies.isEmpty()) {
            System.out.println("There is nothing here to stab.");
            return;
        }

        Enemy targetEnemy = resolveTarget(activeEnemies, args);

        if (targetEnemy == null) {
            return;
        }

        if (targetEnemy.getType() == EnemyType.STANDARD_INFECTED) {
            handleStandardInfected(gameState, targetEnemy);
            return;
        }

        if (targetEnemy.getType() == EnemyType.BRUTE) {
            handleBrute(gameState, targetEnemy);
            return;
        }

        System.out.println("Stabbing that is not the right approach.");
    }

    private void handleStandardInfected(CurrentGameState gameState, Enemy enemy) {
        boolean hasBrokenBottle = gameState.getPlayer().findItemById("broken_bottle") != null;
        boolean hasImprovisedBlade = gameState.getPlayer().findItemById("improvised_blade") != null;

        if (!hasBrokenBottle && !hasImprovisedBlade) {
            System.out.println("You need something sharp to stab the infected.");
            return;
        }

        enemy.defeat();
        System.out.println("You stab the infected and bring it down.");
        ZaunEncounterSystem.advanceIfNeeded(gameState);
    }

    private void handleBrute(CurrentGameState gameState, Enemy enemy) {
        if (gameState.getPlayer().findItemById("improvised_blade") == null) {
            System.out.println("The brute is too large to kill with anything less than a proper improvised weapon.");
            return;
        }

        enemy.defeat();
        System.out.println("You lunge forward with the improvised blade and strike the brute at the throat.");
        System.out.println("The creature staggers, collapses, and finally goes still.");
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

    private Enemy resolveTarget(List<Enemy> enemies, List<String> args) {
        if (args == null || args.isEmpty()) {
            if (enemies.size() == 1) {
                return enemies.get(0);
            }
            System.out.println("Stab what?");
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