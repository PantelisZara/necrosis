package engine.core;

import engine.loader.GameLoader;
import engine.loader.LoadedGameData;
import engine.model.Player;
import engine.model.Room;
import engine.parser.CommandCutter;
import engine.parser.CommandExecutionResult;
import engine.parser.CommandRegistryLoader;
import engine.save.SaveData;
import engine.save.SaveManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GameSession implements GameSessionController {

    private String gameDataPath;
    private LoadedGameData loadedData;
    private CurrentGameState gameState;
    private CommandCutter parser;

    public GameSession(String gameDataPath) {
        resetToGameData(gameDataPath);
    }

    public String getTitle() {
        return loadedData.getTitle();
    }

    public List<String> getIntroLines() {
        return loadedData.getIntroLines();
    }

    public CurrentGameState getGameState() {
        return gameState;
    }

    public boolean executePlayerCommand(String input) {
        String commandInput = normalizeInput(input);
        if (commandInput.isEmpty()) {
            return false;
        }

        CommandExecutionResult result = parser.parseAndExecute(commandInput, gameState);
        if (result.wasExecuted() && result.isReplayable()) {
            gameState.recordCommand(commandInput);
        }

        if (hasReachedEnding()) {
            System.out.println();
            System.out.println("Thanks for playing!");
        }

        return result.wasExecuted();
    }

    @Override
    public void loadSavedGame() throws IOException {
        SaveData saveData = SaveManager.loadSaveData();
        if (saveData == null) {
            throw new IllegalStateException("Save file is empty or invalid.");
        }

        String replayGameDataPath = saveData.getGameDataPath();
        if (replayGameDataPath == null || replayGameDataPath.isBlank()) {
            replayGameDataPath = gameDataPath;
        }

        LoadedGameData replayLoadedData = GameLoader.loadGameData(replayGameDataPath);
        CommandCutter replayParser = createParser(replayLoadedData);
        CurrentGameState replayState = createGameState(replayLoadedData, replayGameDataPath);

        if (saveData.hasReplayHistory()) {
            List<String> replayedCommands = replayCommands(saveData.getCommandHistory(), replayParser, replayState);
            replayState.restoreCommandHistory(replayedCommands);
        } else if (saveData.hasLegacySnapshot()) {
            SaveManager.restoreLegacySnapshot(replayState, saveData);
        } else {
            replayState.restoreCommandHistory(List.of());
        }

        this.gameDataPath = replayGameDataPath;
        this.loadedData = replayLoadedData;
        this.parser = replayParser;
        this.gameState = replayState;
    }

    private void resetToGameData(String gameDataPath) {
        LoadedGameData loadedGameData = GameLoader.loadGameData(gameDataPath);
        this.gameDataPath = gameDataPath;
        this.loadedData = loadedGameData;
        this.parser = createParser(loadedGameData);
        this.gameState = createGameState(loadedGameData, gameDataPath);
    }

    private CommandCutter createParser(LoadedGameData loadedGameData) {
        CommandCutter commandParser = new CommandCutter();
        CommandRegistryLoader.registerCommands(commandParser, loadedGameData.getCommandDefinitions());
        return commandParser;
    }

    private CurrentGameState createGameState(LoadedGameData loadedGameData, String gameDataPath) {
        Map<String, Room> allRooms = loadedGameData.getRooms();
        Room startRoom = allRooms.get(loadedGameData.getStartRoomId());
        if (startRoom == null) {
            throw new IllegalStateException("Start room could not be loaded: " + loadedGameData.getStartRoomId());
        }

        Player player = new Player(startRoom, new ArrayList<>());
        CurrentGameState newGameState = new CurrentGameState(allRooms, player, loadedGameData.getItemTemplates());
        newGameState.setEncounterPhases(loadedGameData.getEncounterPhases());
        newGameState.setGameDataPath(gameDataPath);
        newGameState.setGameSessionController(this);
        return newGameState;
    }

    private List<String> replayCommands(
            List<String> savedCommands,
            CommandCutter replayParser,
            CurrentGameState replayState
    ) {
        List<String> replayedCommands = new ArrayList<>();

        replayState.beginReplay();
        try {
            runReplayOutputControlled(() -> {
                for (String savedCommand : savedCommands) {
                    String commandInput = normalizeInput(savedCommand);
                    if (commandInput.isEmpty()) {
                        continue;
                    }

                    if (!replayParser.isKnownCommand(commandInput)) {
                        throw new IllegalStateException("Saved command is no longer recognized: " + commandInput);
                    }

                    if (!replayParser.isReplayableCommand(commandInput)) {
                        continue;
                    }

                    CommandExecutionResult result = replayParser.parseAndExecute(commandInput, replayState);
                    if (!result.wasExecuted()) {
                        throw new IllegalStateException("Saved command could not be replayed: " + commandInput);
                    }

                    replayedCommands.add(commandInput);
                }
            });
        } finally {
            replayState.endReplay();
        }

        return replayedCommands;
    }

    private void runReplayOutputControlled(Runnable replayAction) {
        PrintStream originalOut = System.out;
        try (PrintStream silentOut = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(silentOut);
            replayAction.run();
        } finally {
            System.setOut(originalOut);
        }
    }

    private String normalizeInput(String input) {
        return input != null ? input.trim() : "";
    }

    private boolean hasReachedEnding() {
        for (String endingFlag : loadedData.getEndingFlags()) {
            if (gameState.isFlagTrue(endingFlag)) {
                return true;
            }
        }

        return false;
    }
}
