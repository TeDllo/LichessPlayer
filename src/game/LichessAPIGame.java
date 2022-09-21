package game;

import board.exceptions.BadMoveException;
import board.Board;
import board.details.Color;
import board.details.Move;
import engine.Engine;
import lichess.LichessClient;

import static handControl.JSONParser.parseField;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class LichessAPIGame implements Game {

    private static final int MOVE_LENGTH = 4;

    private final String nickname;
    private final String GameID;
    private final LichessClient client;
    private final Board board;
    private final Engine engine;

    private boolean repeat = false;

    public LichessAPIGame(String nickname, String GameID, LichessClient client, Board board, Engine engine) {
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
        InputStream in = client.getStreamRequest(GameID);
        String eventJSON = client.extractInputStreamData(in);
        while (eventJSON != null) {
            repeat = false;
            if (!eventJSON.equals("")) {
                processEvent(eventJSON);
            }
            if (!repeat) {
                eventJSON = client.extractInputStreamData(in);
            }
        }
        System.out.println("Game is finished!");
    }

    public void processEvent(String eventJSON) {
        if (isGameFinished(parseField("status", eventJSON, false))) {
            return;
        }

        String type = parseField("type", eventJSON, false);
        String moves = parseField("moves", eventJSON, false);
        if (Objects.equals(type, "gameFull")) {
            processGameFull(moves, getOurColor(eventJSON));
        } else if (!Objects.equals(type, "chatLine")){
            processGameStat(moves);
        }

        System.out.printf("Is our move = %b\n", board.isOurMove());
        if (board.isOurMove()) {
            makeMove();
        }
    }

    private void processGameFull(String moves, Color ourColor) {
        board.setOurColor(ourColor);
        board.insertMoves(moves);
    }

    private void processGameStat(String moves) {
        if (board.isOurMoveByMoves(moves) && !moves.isEmpty()) {
            String lastMove = moves.substring(moves.length() - 4);
            System.out.println("Before:");
            board.showBoard();
            board.makeMove(new Move(lastMove));
            System.out.println("After:");
            board.showBoard();
            System.out.println("Made move: " + lastMove);
        }
    }

    private boolean isGameFinished(String status) {
        return status != null && !status.equals("started");
    }

    private void makeMove() {
        Move nextMove = engine.nextMove(board);
        try {
            client.makeMoveRequest(GameID, nextMove.text);
            board.makeMove(nextMove);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            repeat = true;
        } catch (BadMoveException e) {
            System.err.println("!!!Bad move!!!\n" + nextMove.text);
            board.showBoard();
            repeat = true;
        }
    }

    private Color getOurColor(String eventJSON) {
        String white = parseField("white", eventJSON, true);
        String id = parseField("id", white, false);
        return Objects.equals(id, nickname) ? Color.WHITE : Color.BLACK;
    }
}
