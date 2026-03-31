import engine.commands.InventoryCommand;
import engine.commands.LookCommand;
import engine.commands.TakeCommand;
import engine.core.CurrentGameState;
import engine.loader.GameLoader;
import engine.model.Player;
import engine.model.Room;
import engine.model.Item;
import engine.commands.GoCommand;
import engine.parser.CommandCutter;

import java.util.*;

//early testing game loop
public class Main {

    public static void main(String[] args) {

        Map<String, Room> allRooms = GameLoader.loadRooms("resources/gameData.json");
        Room startRoom = allRooms.get("generator");
        Player player = new Player(startRoom, new ArrayList<>());
        CurrentGameState gameState = new CurrentGameState(allRooms, player);




        CommandCutter parser = new CommandCutter();
        parser.registerCommand("go", new GoCommand());
        parser.registerCommand("look", new LookCommand());
        parser.registerCommand("take", new TakeCommand());
        parser.registerCommand("inv", new InventoryCommand());


        Scanner scanner = new Scanner(System.in);
        System.out.println("\nCold. The first thing you feel is cold. Then the beeping. A heart monitor, disconnected, its battery crying out in three-second intervals. Your eyes open to cracked ceiling tiles and the sharp sting of antiseptic." +
                "A woman's face hovers above you. She is young, tired, wearing scrubs stained with something dark at the cuffs. Her name badge reads: EMILY, R.N.\n" +
                "\n" +
                "\"You're awake,\" she whispers. \"I didn't think you would be.\"");
        System.out.println("Type 'go <direction>' to move.");

        while (true) {

            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Thanks for playing!");
                break;
            }

            parser.parse_Execute(input, gameState);
        }

        scanner.close();
    }
}