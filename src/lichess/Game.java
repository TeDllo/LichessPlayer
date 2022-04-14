package lichess;

import board.Board;
import engine.Engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Game {

    private final String GameID;
    private final LichessClient client;
    private final Board board;
    private final Engine engine;

    public Game(String GameID, LichessClient client, Board board, Engine engine) {
        this.GameID = GameID;
        this.client = client;
        this.board = board;
        this.engine = engine;
    }

    public void start() throws IOException {
        System.out.printf("Game started. GameID: %s.\n", GameID);
        stream();
    }

    private void stream() throws IOException {
        InputStream in = client.streamRequest(GameID);
        String eventJSON = extractData(in);
        while (eventJSON != null) {
            if (!eventJSON.equals("")) {
                process(eventJSON);
            }
            eventJSON = extractData(in);
        }
        System.out.println("Game is finished!");
    }

    private String extractData(InputStream in) throws IOException {
        return new BufferedReader(new InputStreamReader(in,
                StandardCharsets.UTF_8)).readLine();
    }

    public void process(String eventJSON) throws IOException {
        String type = parseField("type", eventJSON, false);
        if (Objects.equals(type, "gameFull")) {
            String white = parseField("white", eventJSON, true);
            String id = parseField("id", white, false);
            if (Objects.equals(id, "TeDllo")) {
                System.out.println("We are white!");
            } else {
                System.out.println("We are black!");
            }
        }

        if (!Objects.equals(type, "chatLine")) {
            String movesLine = parseField("moves", eventJSON, false);
            assert movesLine != null;
            String[] moves = movesLine.split(" ");
            System.out.println(moves.length + " moves were done!");
        }

        if (board.isOurMove()) {
            String nextMove = engine.nextMove(board);
            board.makeMove(nextMove);
            client.makeMoveRequest(GameID, nextMove);
        } else {
            String nextMove = null;
        }
    }

    public static String parseField(String field, String body, boolean isObject) {
        try {
            String end = (isObject ? "}" : "\"");
            int start = body.indexOf(field) + field.length() + 3;
            int stop = body.indexOf(end, start);
            return body.substring(start, stop);
        } catch (Exception e) {
            return null;
        }
    }
}
