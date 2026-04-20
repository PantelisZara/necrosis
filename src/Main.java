import engine.commands.*;
import engine.core.CurrentGameState;
import engine.loader.GameLoader;
import engine.model.Player;
import engine.model.Room;
import engine.parser.CommandCutter;
import engine.loader.LoadedGameData;

import java.util.*;

//early testing game loop
public class Main {

    public static void main() {

        LoadedGameData loadedData = GameLoader.loadGameData("resources/gameData.json");
        Map<String, Room> allRooms = loadedData.getRooms();

        Room startRoom = allRooms.get("exam_room");

        if (startRoom == null) {
            System.out.println("Start room could not be loaded.");
            return;
        }

        Player player = new Player(startRoom, new ArrayList<>());
        CurrentGameState gameState = new CurrentGameState(allRooms, player);

        gameState.setZaunPhases(loadedData.getZaunPhases());

        CommandCutter parser = new CommandCutter();

        parser.registerCommand(new GoCommand(), "go", "move", "run");
        parser.registerCommand(new InventoryCommand(), "inv", "inventory");
        parser.registerCommand(new TakeCommand(), "take", "grab", "hold", "pick up");
        parser.registerCommand(new LookCommand(), "look", "view");
        parser.registerCommand(new LookAtCommand(), "look at", "inspect", "examine");
        parser.registerCommand(new UseCommand(), "use");
        parser.registerCommand(new ReadCommand(), "read");
        parser.registerCommand(new EnterCommand(), "enter");
        parser.registerCommand(new TalkCommand(), "talk", "talk to");
        parser.registerCommand(new ChooseCommand(), "choose");
        parser.registerCommand(new FlashCommand(), "flash");
        parser.registerCommand(new StrikeCommand(), "strike");
        parser.registerCommand(new StabCommand(), "stab");
        parser.registerCommand(new CombineCommand(), "combine");

        Scanner scanner = new Scanner(System.in);
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

