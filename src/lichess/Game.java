package lichess;

import board.Board;
import board.Color;
import engine.Engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

    public void start() throws IOException {
        System.out.printf("Game started. GameID: %s.\n", GameID);

        boolean stop = false;
        while (!stop) {
            try {
                stream();
                stop = true;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void stream() throws IOException {
        InputStream in = client.streamRequest(GameID);
        String eventJSON = extractData(in);
        while (eventJSON != null) {
            repeat = false;
            if (!eventJSON.equals("")) {
                process(eventJSON);
            }
            if (!repeat) {
                eventJSON = extractData(in);
            }
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

    private void nextMove(String moves) throws IOException {
        assert moves != null;
        board.insertMoves(moves);
        System.out.printf("Moves: %s\n", moves);
        if (board.isOurMove()) {
            try {
                String nextMove = engine.nextMove(board);
                client.makeMoveRequest(GameID, nextMove);
                board.makeMove(nextMove);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                repeat = true;
            }
        }
    }

    private Color getColor(String eventJSON) {
        String white = parseField("white", eventJSON, true);
        String id = parseField("id", white, false);
        return Objects.equals(id, nickname) ? Color.WHITE : Color.BLACK;
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
