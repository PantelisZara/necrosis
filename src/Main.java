import engine.core.CurrentGameState;
import engine.loader.GameLoader;
import engine.loader.LoadedGameData;
import engine.model.Player;
import engine.model.Room;
import engine.parser.CommandCutter;
import engine.parser.CommandRegistryLoader;
import ui.GameWindow;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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


        String gameDataPath = args.length > 0 ? args[0] : "resources/gameData.json";
        LoadedGameData loadedData = GameLoader.loadGameData(gameDataPath);

        if (loadedData.getTitle() != null && !loadedData.getTitle().isBlank()) {
            window.setTitle(loadedData.getTitle());
        }

        for (String line : loadedData.getIntroLines()) {
            System.out.println(line);
        }

        Map<String, Room> allRooms = loadedData.getRooms();

        Room startRoom = allRooms.get(loadedData.getStartRoomId());
        if (startRoom == null) {
            System.out.println("Start room could not be loaded: " + loadedData.getStartRoomId());
            return;
        }

        Player player = new Player(startRoom, new ArrayList<>());
        CurrentGameState gameState = new CurrentGameState(allRooms, player, loadedData.getItemTemplates());
        gameState.setEncounterPhases(loadedData.getEncounterPhases());


        CommandCutter parser = new CommandCutter();
        CommandRegistryLoader.registerCommands(parser, loadedData.getCommandDefinitions());


        window.setInputHandler(e -> {
            String input = window.getInput().trim();
            window.clearInput();
            System.out.println("\n");



            if (input.isEmpty()) {
                return;
            }

            boolean commandExecuted = parser.parse_Execute(input, gameState);
            if (commandExecuted) {
                gameState.recordCommand(input);
            }

            if (hasReachedEnding(gameState, loadedData.getEndingFlags())) {

                System.out.println();
                System.out.println("Thanks for playing!");

            }


        }
        );
    }

    private static boolean hasReachedEnding(CurrentGameState gameState, List<String> endingFlags) {
        for (String endingFlag : endingFlags) {
            if (gameState.isFlagTrue(endingFlag)) {
                return true;
            }
        }

        return false;
    }

}
