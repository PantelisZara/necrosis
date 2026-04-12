package engine.commands;

import engine.core.CurrentGameState;
import engine.model.Exit;
import engine.model.Item;
import engine.model.Room;

import java.util.List;

public class UseCommand implements InterfaceCommand {

    @Override
    public void execute(CurrentGameState gameState, List<String> args) {
        if (args == null || args.isEmpty()) {
            System.out.println("Use what?");
            return;
        }

        String target = String.join(" ", args).toLowerCase();
        Room currentRoom = gameState.getPlayer().getCurrentRoom();

        if (target.equals("generator")) {
            useGenerator(gameState, currentRoom);
            return;
        }

        if (target.equals("terminal")) {
            useTerminal(gameState, currentRoom);
            return;
        }

        System.out.println("You can't use that right now.");
    }

    private void useGenerator(CurrentGameState gameState, Room currentRoom) {
        if (!currentRoom.getId().equalsIgnoreCase("generator_room")) {
            System.out.println("There is no generator here.");
            return;
        }

        if (gameState.isFlagTrue("power_restored")) {
            System.out.println("The generator is already running.");
            return;
        }

        Item fuelCanister = gameState.getPlayer().findItemById("fuel_canister");

        if (fuelCanister == null) {
            System.out.println("You need fuel to power the generator.");
            return;
        }

        gameState.setFlag("power_restored", true);
        gameState.getPlayer().removeItem(fuelCanister);

        Room lobby = gameState.getRoom("lobby");
        if (lobby != null) {
            Exit northExit = lobby.getExit("north");
            if (northExit != null) {
                northExit.unlock();
            }
        }

        System.out.println("You pour the fuel into the generator and restart the system.");
        System.out.println("The lights flicker back to life.");
        System.out.println("You hear security doors unlocking somewhere in the facility.");
    }

    private void useTerminal(CurrentGameState gameState, Room currentRoom) {
        if (!currentRoom.getId().equalsIgnoreCase("research_room")) {
            System.out.println("There is no terminal here.");
            return;
        }

        if (!gameState.isFlagTrue("power_restored")) {
            System.out.println("The terminal has no power.");
            return;
        }

        if (gameState.isFlagTrue("terminal_unlocked")) {
            System.out.println("The terminal is already unlocked.");
            return;
        }

        gameState.setFlag("terminal_prompt_active", true);

        System.out.println("The terminal flickers to life.");
        System.out.println("ACCESS CODE REQUIRED");
        System.out.println("Type: enter code <your_code>");

    }
}