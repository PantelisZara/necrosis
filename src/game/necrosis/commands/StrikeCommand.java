package game.necrosis.commands;

import engine.commands.InterfaceCommand;
import engine.core.CurrentGameState;
import engine.model.Enemy;
import engine.model.Room;
import game.necrosis.systems.ZaunEncounterSystem;

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

        Enemy targetEnemy = resolveTarget(activeEnemies, args);

        if (targetEnemy == null) {
            return;
        }

        if (!"RIPPER".equalsIgnoreCase(targetEnemy.getType())) {
            System.out.println("Striking that won't help much.");
            return;
        }

        boolean hasScrapMetal = gameState.getPlayer().findItemById("scrap_metal") != null;
        boolean hasImprovisedBlade = gameState.getPlayer().findItemById("improvised_blade") != null;

        if (!hasScrapMetal && !hasImprovisedBlade) {
            System.out.println("You need something heavy or sharp enough to strike the ripper with.");
            return;
        }

        targetEnemy.defeat();
        if (hasScrapMetal) {
            System.out.println("You swing the scrap metal with all your strength.");
        } else {
            System.out.println("You strike with the improvised blade before the ripper can dodge.");
        }
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

    private Enemy resolveTarget(List<Enemy> enemies, List<String> args) {
        if (args == null || args.isEmpty()) {
            for (Enemy enemy : enemies) {
                if (enemy.getName().equalsIgnoreCase("ripper")) {
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
