import board.Board;
import board.ClassicBoard;
import engine.Engine;
import engine.HumanEngine;
import lichess.Game;
import lichess.LichessClient;

public class MainControl {

    public static String access_token = "lip_EZKvEphkXmkXbdDj6RXW";

    public static void main(String... args) throws Exception {
        LichessClient client = new LichessClient(access_token, true);
        Board board = new ClassicBoard();
        Engine engine = new HumanEngine();

        Game game = new Game("LrId0dkrTTfe", client, board, engine);
        game.start();
    }

}