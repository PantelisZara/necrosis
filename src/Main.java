import engine.commands.*;
import engine.core.CurrentGameState;
import engine.loader.GameLoader;
import engine.model.Player;
import engine.model.Room;
import engine.parser.CommandCutter;

import java.util.*;

//early testing game loop
public class Main {

    public static void main() {

        Map<String, Room> allRooms = GameLoader.loadRooms("resources/gameData.json");
        Room startRoom = allRooms.get("exam_room");
        Player player = new Player(startRoom, new ArrayList<>());
        CurrentGameState gameState = new CurrentGameState(allRooms, player);

        CommandCutter parser = new CommandCutter();

        parser.registerCommand(new FlagsCommand(), "flags");
        parser.registerCommand(new GoCommand(), "go", "move");
        parser.registerCommand(new InventoryCommand(), "inv", "inventory");
        parser.registerCommand(new TakeCommand(), "take", "grab", "hold", "pick up");
        parser.registerCommand(new LookCommand(), "look", "view");
        parser.registerCommand(new LookAtCommand(), "look at", "inspect", "examine");
        parser.registerCommand(new UseCommand(), "use");
        parser.registerCommand(new ReadCommand(), "read");
        parser.registerCommand(new EnterCommand(), "enter");
        parser.registerCommand(new TalkCommand(), "talk", "talk to");
        parser.registerCommand(new DealWithCommand(), "deal with");
        parser.registerCommand(new ChooseCommand(), "choose");

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