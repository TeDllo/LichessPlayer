package lichess;

import board.BadMoveException;
import board.Board;
import board.Color;
import board.Move;
import engine.Engine;

import static lichess.JSONParser.parseField;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Game {

    private final String nickname;
    private final String GameID;
    private final LichessClient client;
    private final Board board;
    private final Engine engine;

    private boolean repeat = false;

    public Game(String nickname, String GameID, LichessClient client, Board board, Engine engine) {
        this.nickname = nickname;
        this.GameID = GameID;
        this.client = client;
        this.board = board;
        this.engine = engine;
    }

    public void start() {
        System.out.printf("Game started. GameID: %s.\n", GameID);

        boolean stop = false;
        while (!stop) {
            try {
                streamGame();
                stop = true;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void streamGame() throws IOException {
        InputStream in = client.streamRequest(GameID);
        String eventJSON = client.extractData(in);
        while (eventJSON != null) {
            repeat = false;
            if (!eventJSON.equals("")) {
                process(eventJSON);
            }
            if (!repeat) {
                eventJSON = client.extractData(in);
            }
        }
        System.out.println("Game is finished!");
    }

    public void process(String eventJSON) {
        String type = parseField("type", eventJSON, false);
        if (Objects.equals(type, "gameFull")) {
            board.setOurColor(getColor(eventJSON));
        }

        String status = parseField("status", eventJSON, false);
        if (status != null && !status.equals("started")) {
            System.out.printf("Status: %s.\n", status);
            return;
        }

        if (!Objects.equals(type, "chatLine")) {
            nextMove(parseField("moves", eventJSON, false));
        }
    }

    private void nextMove(String moves) {
        assert moves != null;
        board.insertMoves(moves);
        System.out.printf("Moves: %s\n", moves);
        if (board.isOurMove()) {
            try {
                Move nextMove = engine.nextMove(board);
                client.makeMoveRequest(GameID, nextMove.text);
                board.appendMove(nextMove);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                repeat = true;
            } catch (BadMoveException e) {
                System.err.println("Bad move");
                repeat = true;
            }
        }
    }

    private Color getColor(String eventJSON) {
        String white = parseField("white", eventJSON, true);
        String id = parseField("id", white, false);
        return Objects.equals(id, nickname) ? Color.WHITE : Color.BLACK;
    }
}
