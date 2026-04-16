package engine.systems;

import engine.core.CurrentGameState;
import engine.model.*;

public class ZaunEncounterSystem {

    public static void startEncounter(CurrentGameState gameState) {
        if (gameState.isFlagTrue("zaun_encounter_started")) {
            return;
        }

        gameState.setFlag("zaun_encounter_started", true);
        gameState.setZaunPhase(1);

        spawnPhase(gameState, 1);
    }

    public static void advanceIfNeeded(CurrentGameState gameState) {
        if (!gameState.isFlagTrue("zaun_encounter_started")) {
            return;
        }

        Room bossRoom = gameState.getRoom("boss_room");
        if (bossRoom == null) {
            return;
        }

        if (bossRoom.hasActiveEnemies()) {
            return;
        }

        int currentPhase = gameState.getZaunPhase();

        if (currentPhase == 1) {
            gameState.setZaunPhase(2);
            spawnPhase(gameState, 2);
            return;
        }

        if (currentPhase == 2) {
            gameState.setZaunPhase(3);
            spawnPhase(gameState, 3);
            return;
        }

        if (currentPhase == 3) {
            gameState.setFlag("zaun_defeated", true);
            gameState.setFlag("final_choice_available", true);

            System.out.println("Silence falls across the chamber.");
            System.out.println("Zaun lowers his head and steps forward, unarmed.");
            System.out.println("\"So... you survived them all. Now make your choice.\"");
            System.out.println("Type: choose kill / choose spare / choose join");
        }
    }

    private static void spawnPhase(CurrentGameState gameState, int phaseNumber) {
        Room bossRoom = gameState.getRoom("boss_room");
        if (bossRoom == null) {
            return;
        }

        ZaunPhase phase = gameState.getZaunPhases()
                .stream()
                .filter(p -> p.getPhase() == phaseNumber)
                .findFirst()
                .orElse(null);

        if (phase == null) {
            System.out.println("No data found for Zaun phase " + phaseNumber + ".");
            return;
        }

        System.out.println(phase.getMessage());

        for (EnemySpawn spawn : phase.getEnemies()) {
            EnemyType enemyType = EnemyType.valueOf(spawn.getType().toUpperCase());

            for (int i = 1; i <= spawn.getCount(); i++) {
                String generatedId = "zaun_phase" + phaseNumber + "_" + spawn.getType().toLowerCase() + "_" + i;
                String generatedName = spawn.getType().toLowerCase().replace("_", " ");
                String generatedDescription = "A hostile " + generatedName + " attacks.";

                bossRoom.addEnemy(new Enemy(
                        generatedId,
                        generatedName,
                        generatedDescription,
                        enemyType
                ));
            }
        }
    }
}