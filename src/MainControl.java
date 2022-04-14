import board.Board;
import board.ClassicBoard;
import engine.Engine;
import engine.HumanEngine;
import lichess.Game;
import lichess.LichessClient;

public class MainControl {

    public static String access_token = "lip_EZKvEphkXmkXbdDj6RXW";
    public static String nickname = "TeDllo";
    public static String gameID = "E89GfR95HsbW";

    public static void main(String... args) throws Exception {
        LichessClient client = new LichessClient(access_token, false);
        Board board = new ClassicBoard();
        Engine engine = new HumanEngine();

        nickname = nickname.toLowerCase();

        Game game = new Game(nickname, gameID, client, board, engine);
        game.start();
    }

}