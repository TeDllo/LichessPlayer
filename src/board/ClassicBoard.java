package board;

public class ClassicBoard implements Board {

    private Cell[][] field;
    private Color turn;

    public ClassicBoard() {
        field = new Cell[8][8];
    }

    @Override
    public void insertMoves(String moves) {
    }

    @Override
    public void showBoard() {
    }

    @Override
    public Color turn() {
        return this.turn;
    }

    @Override
    public void makeMove(String move) {
    }

    @Override
    public boolean isOurMove() {
        return false;
    }

    @Override
    public boolean correctMove(String move) {
        return false;
    }

    @Override
    public boolean isCheck() {
        return false;
    }

    @Override
    public boolean isMate() {
        return false;
    }
}
