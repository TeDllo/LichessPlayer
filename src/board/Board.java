package board;

public interface Board {
    void makeMove(String move);

    void insertMoves(String moves);

    boolean correctMove(String move);

    boolean isCheck();

    boolean isMate();

    boolean isOurMove();

    Color turn();

    void showBoard();
}
