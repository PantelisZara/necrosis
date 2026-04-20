import engine.commands.*;
import engine.core.CurrentGameState;
import engine.loader.GameLoader;
import engine.loader.LoadedGameData;
import engine.model.Player;
import engine.model.Room;
import engine.parser.CommandCutter;
import ui.GameWindow;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;


public class Main {

    public static void main(String[] args) {


        GameWindow window = new GameWindow();
        window.setVisible(true);


        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                window.appendOutput(String.valueOf((char) b));
            }
        }));


        LoadedGameData loadedData = GameLoader.loadGameData("resources/gameData.json");

        for (String line : loadedData.getIntroLines()) {
            System.out.println(line);
        }

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


        window.setInputHandler(e -> {
            String input = window.getInput().trim();
            window.clearInput();
            System.out.println("\n");



            if (input.isEmpty()) {
                return;
            }


            if (input.equalsIgnoreCase("quit")) {
                System.out.println("Thanks for playing!");
                System.exit(0);
            }

            parser.parse_Execute(input, gameState);


            if (gameState.isFlagTrue("ending_survival")
                    || gameState.isFlagTrue("ending_cure")
                    || gameState.isFlagTrue("ending_evolution")) {

                System.out.println();
                System.out.println("Thanks for playing!");

            }


        }
        );
    }

}