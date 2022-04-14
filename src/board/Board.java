package board;

public interface Board {
    void appendMove(String move);

    void insertMoves(String moves);

    boolean correctMove(String move);

    boolean isCheck(Color side);

    boolean isMate();

    boolean isOurMove();

    Color turn();

    void showBoard();

    void setOurColor(Color color);
}
