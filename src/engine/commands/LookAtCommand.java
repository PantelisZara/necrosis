package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Enemy;
import engine.model.Interactable;
import engine.model.Item;
import engine.model.Npc;
import engine.model.Room;

import java.util.List;

public class LookAtCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Look at what?");
            return;
        }

        String targetName = normalizeTarget(String.join(" ", args));
        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        for (Item item : currentRoom.getItems()) {
            if (matches(targetName, item.getId(), item.getName())) {
                System.out.println(item.getDescription());
                return;
            }
        }

        for (Item item : gameState.getPlayer().getInventory()) {
            if (matches(targetName, item.getId(), item.getName())) {
                System.out.println(item.getDescription());
                return;
            }
        }

        for (Interactable interactable : currentRoom.getInteractables()) {
            if (matches(targetName, interactable.getId(), interactable.getName())) {
                System.out.println(interactable.getDescription());
                return;
            }
        }

        for (Npc npc : currentRoom.getNpcs()) {
            if (matches(targetName, npc.getId(), npc.getName())) {
                System.out.println(npc.getDescription());
                return;
            }
        }

        for (Enemy enemy : currentRoom.getEnemies()) {
            if (matches(targetName, enemy.getId(), enemy.getName())) {
                System.out.println(enemy.getDescription());
                return;
            }
        }

        System.out.println("You don't see that here.");
    }

    private String normalizeTarget(String target) {
        String normalized = target.toLowerCase().trim();
        if (normalized.startsWith("the ")) {
            return normalized.substring(4);
        }
        if (normalized.startsWith("a ")) {
            return normalized.substring(2);
        }
        if (normalized.startsWith("an ")) {
            return normalized.substring(3);
        }

        return normalized;
    }

    private boolean matches(String target, String id, String name) {
        return matchesValue(target, id) || matchesValue(target, name);
    }

    private boolean matchesValue(String target, String value) {
        if (value == null) {
            return false;
        }

        String normalizedValue = value.toLowerCase().replace("_", " ");
        return target.equals(normalizedValue)
                || target.equals(normalizedValue + "s")
                || normalizedValue.endsWith(" " + target)
                || normalizedValue.startsWith(target + " ");
    }
}
