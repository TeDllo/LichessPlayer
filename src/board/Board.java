package board;

import board.details.Cell;
import board.details.Color;
import board.details.Move;

public interface Board {

    Cell[][] getField();

    void appendMove(Move move);

    void insertMoves(String moves);

    boolean correctMove(Move move);

    boolean isCheck(Color side);

    boolean isMate();

    boolean isOurMove();

    Color turn();

    void showBoard();

    void setOurColor(Color color);
}
