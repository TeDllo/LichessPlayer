package game;

import board.Board;
import board.ClassicBoard;
import board.details.Color;
import board.details.Move;
import engine.Engine;
import handControl.HandImitator;
import handControl.LichessHTMLParser;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

public class HandGame implements Game {

    private final String nickname;
    private Board board;
    private final Engine engine;
    private final HandImitator imitator;
    private final String gameID;

    private boolean first = true;

    public HandGame(String nickname, Board board, Engine engine, HandImitator imitator, String gameID) {
        this.nickname = nickname;
        this.board = board;
        this.engine = engine;
        this.imitator = imitator;
        this.gameID = gameID;
    }

    @Override
    public void start() {
        try {
            imitator.setGame(gameID);

            String htmlPage = imitator.getHTMLPage();

            while (!LichessHTMLParser.isGameFinished(htmlPage)) {
                if (first) {
                    Color ourColor = LichessHTMLParser.getOurColor(htmlPage, nickname);
                    imitator.setColor(ourColor == Color.WHITE);
                    board.setOurColor(ourColor);
                    first = false;
                }
                initMoves(htmlPage);

                htmlPage = imitator.getHTMLPage();

                Thread.sleep(1000);
            }

            System.out.println("Game is finished!");
//            imitator.returnToTournament();

        } catch (IOException | UnsupportedFlavorException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initMoves(String htmlPage) {
        board = new ClassicBoard();
        board.setOurColor(LichessHTMLParser.getOurColor(htmlPage, nickname));
        ArrayList<Move> moves = LichessHTMLParser.getAllMoves(htmlPage);
        for (Move move : moves) {
            board.makeMove(move);
        }
        System.out.println("Our color:");
        System.out.println(LichessHTMLParser.getOurColor(htmlPage, nickname));
        System.out.println("Is our color: " + board.isOurMove());
        if (board.isOurMove()) {
            makeOurMove();
        }
    }

    private void makeOurMove() {
        Move nextMove = engine.nextMove(board);
        board.makeMove(nextMove);
        imitator.makeChessMove(nextMove);
    }
}
