package engine.commands;

import engine.core.CurrentGameState;
import engine.model.DialogueEntry;
import engine.model.Npc;
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

        if (engine.systems.ThreatSystem.triggerEnemyAttack(gameState, "talk")) {
            return;
        }

        String target = String.join(" ", args).toLowerCase().trim();

        if (target.startsWith("to ")) {
            target = target.substring(3);
        }

        Room currentRoom = gameState.getPlayer().getCurrentRoom();
        Npc npc = currentRoom.findNpcByName(target);

        if (npc == null) {
            System.out.println("There is no one here by that name.");
            return;
        }

        DialogueEntry matchingDialogue = findMatchingDialogue(gameState, npc);

        if (matchingDialogue != null) {
            System.out.println(matchingDialogue.getText());
        } else {
            System.out.println(npc.getName() + " has nothing to say right now.");
        }

        if (npc.getId().equalsIgnoreCase("zaun") &&
                !gameState.isFlagTrue("zaun_encounter_started")) {
            System.out.println();
            System.out.println("The chamber trembles as containment locks disengage.");
            ZaunEncounterSystem.startEncounter(gameState);
        }
    }

    private DialogueEntry findMatchingDialogue(CurrentGameState gameState, Npc npc) {
        for (DialogueEntry dialogue : npc.getDialogues()) {
            boolean requiredOk = dialogue.getRequiredFlag() == null
                    || gameState.isFlagTrue(dialogue.getRequiredFlag());

            boolean forbiddenOk = dialogue.getForbiddenFlag() == null
                    || !gameState.isFlagTrue(dialogue.getForbiddenFlag());

            if (requiredOk && forbiddenOk) {
                return dialogue;
            }
        }
        return null;
    }
}