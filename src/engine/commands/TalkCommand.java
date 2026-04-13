package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Room;
import engine.systems.ZaunEncounterSystem;

import java.util.List;

public class TalkCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Talk to whom?");
            return;
        }

        String target = String.join(" ", args).toLowerCase().trim();

        if (target.equals("emily")) {
            talkToEmily(gameState);
            return;
        }

        if (target.equals("zaun")) {
            talkToZaun(gameState);
            return;
        }

        System.out.println("There is no one here by that name.");
    }

    private void talkToZaun(CurrentGameState gameState) {
        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        if (!currentRoom.getId().equalsIgnoreCase("boss_room")) {
            System.out.println("Dr. Zaun is not here.");
            return;
        }

        if (gameState.isFlagTrue("zaun_defeated")) {
            System.out.println("Zaun lies motionless. It's over.");
            return;
        }

        if (!gameState.isFlagTrue("zaun_encounter_started")) {
            System.out.println("A figure emerges from the shadows.");
            System.out.println("Dr. Zaun steps forward, his eyes hollow yet intense.");
            System.out.println("\"You finally made it,\" he says calmly.");
            System.out.println("\"Do you understand what you've done? What this place was meant to become?\"");
            System.out.println("\"You think this is a disaster... but it's evolution.\"");
            System.out.println();
            System.out.println("The chamber trembles as containment locks disengage.");

            ZaunEncounterSystem.startEncounter(gameState);
            return;
        }

        System.out.println("Zaun watches you silently, waiting.");
    }

    private void talkToEmily(CurrentGameState gameState) {
        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        String roomId = currentRoom.getId();

        boolean powerRestored = gameState.isFlagTrue("power_restored");
        boolean terminalUnlocked = gameState.isFlagTrue("terminal_unlocked");

        if (roomId.equalsIgnoreCase("exam_room") && powerRestored && !terminalUnlocked) {
            System.out.println("Emily is searching through a damaged medical cabinet.");
            System.out.println("\"I came here looking for antibiotics,\" she says.");
            System.out.println("\"The research logs might tell us what Zaun was hiding. Try the terminal.\"");
            return;
        }

        if (roomId.equalsIgnoreCase("exam_room") && terminalUnlocked) {
            System.out.println("Emily reads the glowing terminal screen and goes pale.");
            System.out.println("\"So it was true... NV-1 wasn't an accident,\" she whispers.");
            System.out.println("\"Zaun did this. We need to stop him before he reaches the final stage.\"");
            return;
        }

        if (roomId.equalsIgnoreCase("exam_room")) {
            System.out.println("Emily looks exhausted, but relieved that you are awake.");
            System.out.println("\"The whole facility collapsed in a matter of hours,\" she says quietly.");
            System.out.println("\"If we can restore power in the generator room, we might still have a chance.\"");
            System.out.println("\"And if you see Dr. Zaun... don't listen to him.\"");
            return;
        }

        System.out.println("Emily is not here.");
    }
}