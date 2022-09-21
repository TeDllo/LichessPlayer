package board;

import board.details.*;

import java.util.ArrayList;

public interface Board {

    Cell[][] getField();

    Cell getCell(int letter, int digit);

    ArrayList<Point> getPossibleMoves(Figure figure, Point from);

    void makeMove(Move move);

    void revertMove(Move move, Cell eater, Cell food, boolean isCastling);

    void insertMoves(String moves);

    boolean correctMove(Move move);

    boolean isCheck(Color side);

    boolean isMate(Color side);

    boolean isOurMove();

    Color turn();

    void showBoard();

    void setOurColor(Color color);

    boolean isOurMoveByMoves(String moves);

    boolean isCastling(Move move);

    String notation();
}

