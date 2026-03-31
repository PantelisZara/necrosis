package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Item;

import java.util.List;

public class InventoryCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        List<Item> inventory = gameState.getPlayer().getInventory();

        if (inventory == null || inventory.isEmpty()) {
            System.out.println("Your inventory is empty.");
            return;
        }

        System.out.println("You are carrying:");
        for (Item item : inventory) {
            System.out.println("- " + item.getName());
        }
    }
}