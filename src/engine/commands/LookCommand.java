package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Exit;
import engine.model.Item;
import engine.model.Room;
import engine.model.Enemy;
import engine.model.Npc;

import java.util.List;
import java.util.Map;

public class LookCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        System.out.println(currentRoom.getDescription());

        if (gameState.getPlayer().isInjured()) {
            System.out.println("You are bleeding.");
        }

        List<Item> items = currentRoom.getItems();
        if (items == null || items.isEmpty()) {
            System.out.println("There are no items here.");
        } else {
            System.out.println("You see:");
            for (Item item : items) {
                System.out.println("- " + item.getName());
            }
        }

        Map<String, Exit> exits = currentRoom.getExits();
        if (exits == null || exits.isEmpty()) {
            System.out.println("There are no exits.");
        } else {
            System.out.println("Exits:");
            for (String direction : exits.keySet()) {
                System.out.println("- " + direction);
            }
        }

        if (currentRoom.getEnemies() != null && !currentRoom.getEnemies().isEmpty()) {
            boolean hasActiveEnemies = false;

            for (Enemy enemy : currentRoom.getEnemies()) {
                if (!enemy.isDefeated()) {
                    if (!hasActiveEnemies) {
                        System.out.println("Enemies here:");
                        hasActiveEnemies = true;
                    }
                    System.out.println("- " + enemy.getName());
                }
            }
        }
        for (Npc npc : currentRoom.getNpcs()) {
            System.out.println("Npcs:");
            System.out.println("- " + npc.getName());
        }



    }
}