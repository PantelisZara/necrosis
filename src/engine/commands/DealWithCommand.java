package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Enemy;
import engine.model.EnemyType;
import engine.model.Room;
import engine.systems.ZaunEncounterSystem;

import java.util.List;

public class DealWithCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Deal with what?");
            return;
        }

        String targetName = String.join(" ", args).toLowerCase();
        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        Enemy enemy = currentRoom.findActiveEnemyByName(targetName);

        if (enemy == null) {
            System.out.println("There is no active enemy here by that name.");
            return;
        }

        EnemyType type = enemy.getType();

        switch (type) {
            case STANDARD_INFECTED:
                handleStandardInfected(gameState, enemy);
                break;

            case CLICKER:
                handleClicker(gameState, enemy);
                break;

            case RIPPER:
                handleRipper(gameState, enemy);
                break;

            default:
                System.out.println("You don't know how to deal with that.");
        }
    }

    private void handleStandardInfected(CurrentGameState gameState, Enemy enemy) {
        enemy.defeat();
        System.out.println("You overpower the infected after a desperate struggle.");
        ZaunEncounterSystem.advanceIfNeeded(gameState);
    }

    private void handleClicker(CurrentGameState gameState, Enemy enemy) {
        if (gameState.getPlayer().findItemById("flashlight") == null) {
            System.out.println("The clicker twitches toward the slightest sound.");
            System.out.println("You need something to disorient it first.");
            return;
        }

        enemy.defeat();
        System.out.println("You flash the light directly into the creature's face.");
        System.out.println("It recoils in confusion, giving you enough time to get past it.");
        ZaunEncounterSystem.advanceIfNeeded(gameState);
    }

    private void handleRipper(CurrentGameState gameState, Enemy enemy) {
        if (gameState.getPlayer().findItemById("scrap_metal") == null) {
            System.out.println("The ripper lunges low and fast.");
            System.out.println("You need something sharp or heavy to defend yourself.");
            return;
        }

        enemy.defeat();
        System.out.println("You brace yourself and strike with the scrap metal.");
        System.out.println("The ripper collapses with a final shriek.");
        ZaunEncounterSystem.advanceIfNeeded(gameState);
    }
}