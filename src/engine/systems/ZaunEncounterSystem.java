package engine.systems;

import engine.core.CurrentGameState;
import engine.model.Enemy;
import engine.model.EnemyType;
import engine.model.Room;

public class ZaunEncounterSystem {

    public static void startEncounter(CurrentGameState gameState) {
        if (gameState.isFlagTrue("zaun_encounter_started")) {
            return;
        }

        gameState.setFlag("zaun_encounter_started", true);
        gameState.setZaunPhase(1);

        Room bossRoom = gameState.getRoom("boss_room");
        if (bossRoom != null) {
            spawnPhaseEnemies(bossRoom, 1);
        }
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
            spawnPhaseEnemies(bossRoom, 2);
            System.out.println("Zaun's voice echoes through the lab:");
            System.out.println("\"Their sacrifice was not in vain. The Catalyst is forming in you. I can see it.\"");
            return;
        }

        if (currentPhase == 2) {
            gameState.setZaunPhase(3);
            spawnPhaseEnemies(bossRoom, 3);
            System.out.println("Zaun laughs softly.");
            System.out.println("\"Kill them. Kill them all. Every death strengthens the Catalyst.\"");
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

    private static void spawnPhaseEnemies(Room bossRoom, int phase) {
        if (phase == 1) {
            bossRoom.addEnemy(new Enemy(
                    "zaun_phase1_infected_1",
                    "zombified human",
                    "A shambling infected staggers toward you.",
                    EnemyType.STANDARD_INFECTED
            ));
            bossRoom.addEnemy(new Enemy(
                    "zaun_phase1_infected_2",
                    "zombified human",
                    "Its skin hangs in torn sheets as it advances.",
                    EnemyType.STANDARD_INFECTED
            ));
            bossRoom.addEnemy(new Enemy(
                    "zaun_phase1_infected_3",
                    "zombified human",
                    "It lets out a broken moan and lunges.",
                    EnemyType.STANDARD_INFECTED
            ));

            System.out.println("Zaun releases the first wave.");
            System.out.println("Three zombified humans stagger out from shattered glass enclosures.");
        }

        if (phase == 2) {
            bossRoom.addEnemy(new Enemy(
                    "zaun_phase2_clicker_1",
                    "clicker",
                    "A blind creature clicks sharply, tilting its head toward you.",
                    EnemyType.CLICKER
            ));
            bossRoom.addEnemy(new Enemy(
                    "zaun_phase2_clicker_2",
                    "clicker",
                    "Its calcified face twitches as it listens for movement.",
                    EnemyType.CLICKER
            ));

            System.out.println("Zaun opens another containment line.");
            System.out.println("Two clickers emerge, heads twitching toward the slightest sound.");
        }

        if (phase == 3) {
            for (int i = 1; i <= 4; i++) {
                bossRoom.addEnemy(new Enemy(
                        "zaun_phase3_ripper_" + i,
                        "ripper",
                        "A hunched, doglike infected snarls and scrapes across the floor.",
                        EnemyType.RIPPER
                ));
            }

            System.out.println("Metal locks snap open around the room.");
            System.out.println("A pack of rippers bursts from the lower cages.");
        }
    }
}