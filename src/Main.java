import engine.core.GameSession;
import ui.GameWindow;

import java.io.OutputStream;
import java.io.PrintStream;


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
        GameSession gameSession;
        try {
            gameSession = new GameSession(gameDataPath);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (gameSession.getTitle() != null && !gameSession.getTitle().isBlank()) {
            window.setGameTitle(gameSession.getTitle());
        }

        for (String line : gameSession.getIntroLines()) {
            System.out.println(line);
        }

        window.setInputHandler(e -> {
            String input = window.getInput().trim();
            window.clearInput();
            System.out.println("\n");

            if (input.isEmpty()) {
                return;
            }

            gameSession.executePlayerCommand(input);
        }
        );
    }
}
