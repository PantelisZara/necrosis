package engine.systems;

import engine.core.CurrentGameState;
import engine.model.Room;

public class ThreatSystem {

    public static boolean roomHasActiveEnemies(CurrentGameState gameState) {
        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        return currentRoom != null && currentRoom.hasActiveEnemies();
    }

    public static boolean triggerEnemyAttack(CurrentGameState gameState, String actionName) {
        if (!roomHasActiveEnemies(gameState)) {
            return false;
        }

        if (gameState.getPlayer().isInjured()) {
            System.out.println("You try to " + actionName + ", but the enemies keep closing in.");
            System.out.println("You are already badly wounded and losing blood.");
            gameState.getPlayer().decrementDeathCounter();

            if (!gameState.getPlayer().isAlive()) {
                System.out.println("You collapse before you can react.");
                System.out.println("GAME OVER");
            }

            return true;
        }

        System.out.println("You try to " + actionName + ", but an enemy strikes before you can finish.");
        System.out.println("You are badly injured. Use bandages immediately or you will die.");
        gameState.getPlayer().injure();
        return true;
    }
}