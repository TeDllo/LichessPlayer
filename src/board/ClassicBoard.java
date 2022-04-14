package board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassicBoard implements Board {

    private Color ourColor;

    private Cell[][] field;

    private ArrayList<String> moves;

    public ClassicBoard() {
        field = new Cell[8][8];
        moves = new ArrayList<>();
    }

    @Override
    public void insertMoves(String moves) {
        if (!moves.isEmpty()) {
            this.moves = new ArrayList<>(Arrays.asList(moves.split(" ")));
        }
    }

    @Override
    public void showBoard() {
    }

    @Override
    public Color turn() {
        return (moves.size() % 2 == 0 ? Color.WHITE : Color.BLACK);
    }

    @Override
    public void setOurColor(Color color) {
        System.out.printf("Our color is %s.\n", color.name());
        ourColor = color;
    }

    @Override
    public void makeMove(String move) {
        moves.add(move);
    }

    @Override
    public boolean isOurMove() {
        return (ourColor == Color.WHITE) == (moves.size() % 2 == 0);
    }

    @Override
    public boolean correctMove(String move) {
        return true;
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
