package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Exit;
import engine.model.Enemy;
import engine.model.Interactable;
import engine.model.Item;
import engine.model.Npc;
import engine.model.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LookCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        System.out.println(currentRoom.getDescription());
        System.out.println();

        if (gameState.getPlayer().isInjured()) {
            System.out.println("Status:");
            System.out.println("You are bleeding.");
            System.out.println();
        }

        printItems(currentRoom.getItems());
        printNpcs(currentRoom.getNpcs());
        printInteractables(currentRoom.getInteractables());
        printEnemies(currentRoom.getEnemies());
        printExits(currentRoom.getExits());
        printHints(currentRoom.getHints());
    }

    private void printItems(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        System.out.println("Items here:");
        for (Item item : items) {
            System.out.println("- " + item.getName());
        }
        System.out.println();
    }

    private void printNpcs(List<Npc> npcs) {
        if (npcs == null || npcs.isEmpty()) {
            return;
        }

        System.out.println("NPCs here:");
        for (Npc npc : npcs) {
            System.out.println("- " + npc.getName());
        }
        System.out.println();
    }

    private void printInteractables(List<Interactable> interactables) {
        if (interactables == null || interactables.isEmpty()) {
            return;
        }

        System.out.println("Interactables:");
        for (Interactable interactable : interactables) {
            System.out.println("- " + interactable.getName());
        }
        System.out.println();
    }

    private void printEnemies(List<Enemy> enemies) {
        if (enemies == null || enemies.isEmpty()) {
            return;
        }

        List<Enemy> activeEnemies = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (!enemy.isDefeated()) {
                activeEnemies.add(enemy);
            }
        }

        if (activeEnemies.isEmpty()) {
            return;
        }

        System.out.println("Enemies:");
        for (Enemy enemy : activeEnemies) {
            System.out.println("- " + enemy.getName());
        }
        System.out.println();
    }

    private void printExits(Map<String, Exit> exits) {
        if (exits == null || exits.isEmpty()) {
            System.out.println("Exits:");
            System.out.println("- none");
            System.out.println();
            return;
        }

        List<String> directions = new ArrayList<>(exits.keySet());
        Collections.sort(directions);

        System.out.println("Exits:");
        for (String direction : directions) {
            System.out.println("- " + direction);
        }
        System.out.println();
    }

    private void printHints(List<String> hints) {
        if (hints == null || hints.isEmpty()) {
            return;
        }

        System.out.println("Hint:");
        for (String hint : hints) {
            System.out.println(hint);
        }
    }
}
