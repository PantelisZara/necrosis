package game.necrosis.commands;

import engine.commands.TalkCommand;
import engine.core.CurrentGameState;
import engine.model.Npc;
import game.necrosis.systems.ZaunEncounterSystem;

public class NecrosisTalkCommand extends TalkCommand {

    @Override
    protected void afterTalk(CurrentGameState gameState, Npc npc) {
        if (npc.getId().equalsIgnoreCase("zaun") &&
                !gameState.isFlagTrue("zaun_encounter_started")) {
            System.out.println();
            System.out.println("The chamber trembles as containment locks disengage.");
            ZaunEncounterSystem.startEncounter(gameState);
        }
    }
}
