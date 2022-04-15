package board;

public interface Board {
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
