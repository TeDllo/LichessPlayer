package board;

import board.details.Cell;
import board.details.Color;
import board.details.Move;

public interface Board {

    Cell[][] getField();

    void makeMove(Move move);

    void insertMoves(String moves);

    boolean correctMove(Move move);

    boolean isCheck(Color side);

    boolean isMate(Color side);

    boolean isOurMove();

    Color turn();

    void showBoard();

    void setOurColor(Color color);

    boolean isOurMoveByMoves(String moves);
}
