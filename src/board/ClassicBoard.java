package board;

import java.util.ArrayList;
import java.util.Arrays;

import static board.Figure.*;
import static board.Color.*;


public class ClassicBoard implements Board {

    private Color ourColor;
    private Cell[][] field;
    private ArrayList<String> moves;

    public ClassicBoard() {
        moves = new ArrayList<>();
        initField();
    }

    @Override
    public void insertMoves(String moves) {
        if (!moves.isEmpty()) {
            this.moves = new ArrayList<>(Arrays.asList(moves.split(" ")));
        }
        updateField();
    }

    @Override
    public void appendMove(String move) {
        moves.add(move);
        makeMove(move);
    }

    @Override
    public void showBoard() {
        System.out.printf("---%s's turn.---\n", turn().name());
        updateField();
        int reverse = (turn() == Color.BLACK ? 9 : 0);
        for (int digit = 8; digit > 0; digit--) {
            System.out.print(Math.abs(reverse - digit) + " | ");
            for (int letter = 1; letter < 9; letter++) {
                System.out.print(
                        field[Math.abs(reverse - letter)][Math.abs(reverse - digit)] + " "
                );
            }
            System.out.println();
        }
        System.out.println("--|----------------");
        System.out.print("  | ");
        for (int letter = 1; letter < 9; letter++) {
            System.out.print(Character.toString(Math.abs(reverse - letter) + 'a' - 1) + " ");
        }
        System.out.println();
    }

    @Override
    public Color turn() {
        return (moves.size() % 2 == 0 ? WHITE : Color.BLACK);
    }

    @Override
    public void setOurColor(Color color) {
        System.out.printf("Our color is %s.\n", color.name());
        ourColor = color;
    }

    @Override
    public boolean isOurMove() {
        return (ourColor == WHITE) == (moves.size() % 2 == 0);
    }

    @Override
    public boolean correctMove(String move) {
        int letterFrom = move.charAt(0) - 'a' + 1;
        int digitFrom = move.charAt(1) - '0';
        int letterTo = move.charAt(2) - 'a' + 1;
        int digitTo = move.charAt(3) - '0';

        boolean isMove = (letterFrom != letterTo || digitFrom != digitTo);
        boolean hasFigure = field[letterFrom][digitFrom].color != EMPTY;
        boolean hasAbility = checkAbility(move);
        boolean hasRights = checkRights(move);

        return isMove && hasFigure && hasAbility && hasRights;
    }

    @Override
    public boolean isCheck(Color side) {
        return false;
    }

    @Override
    public boolean isMate() {
        return false;
    }

    private void updateField() {
        initField();
        for (String move : moves) {
            makeMove(move);
        }
    }

    private void makeMove(String move) {
        int letterFrom = move.charAt(0) - 'a' + 1;
        int digitFrom = move.charAt(1) - '0';
        int letterTo = move.charAt(2) - 'a' + 1;
        int digitTo = move.charAt(3) - '0';

        field[letterTo][digitTo].figure = field[letterFrom][digitFrom].figure;
        field[letterTo][digitTo].color = field[letterFrom][digitFrom].color;

        field[letterFrom][digitFrom].color = EMPTY;

        // Checking transforming into QUEEN
        if (digitTo == 8 && field[letterTo][digitTo].color == WHITE
                || digitTo == 1 && field[letterTo][digitTo].color == BLACK) {
            field[letterTo][digitTo].figure = QUEEN;
        }
    }

    private void initField() {
        field = new Cell[9][9];
        for (int digit = 1; digit <= 8; digit++) {
            for (int letter = 1; letter <= 8; letter++) {
                field[letter][digit] = new Cell();

                if (3 <= digit && digit <= 6) {
                    field[letter][digit].color = EMPTY;
                } else {
                    if (digit == 2 || digit == 7) {
                        field[letter][digit].figure = PAWN;
                    } else {
                        switch (letter) {
                            case 1:
                            case 8:
                                field[letter][digit].figure = ROOK;
                                break;
                            case 2:
                            case 7:
                                field[letter][digit].figure = NIGHT;
                                break;
                            case 3:
                            case 6:
                                field[letter][digit].figure = BISHOP;
                                break;
                            case 4:
                                field[letter][digit].figure = QUEEN;
                                break;
                            case 5:
                                field[letter][digit].figure = KING;
                                break;
                        }
                    }

                    field[letter][digit].color = (digit <= 2 ? WHITE : BLACK);
                }
            }
        }
    }

    private boolean checkRights(String move) {
        return true;
    }

    private boolean checkAbility(String move) {
        int letterFrom = move.charAt(0) - 'a' + 1;
        int digitFrom = move.charAt(1) - '0';

        Figure figure = field[letterFrom][digitFrom].figure;
        switch (figure) {
            case KING:
                return kingAbility(move);
            case QUEEN:
                return queenAbility(move);
            case BISHOP:
                return bishopAbility(move);
            case NIGHT:
                return nightAbility(move);
            case ROOK:
                return rookAbility(move);
            default:
                return pawnAbility(move);
        }
    }

    private boolean pawnAbility(String move) {
        int letterFrom = move.charAt(0) - 'a' + 1;
        int digitFrom = move.charAt(1) - '0';
        int letterTo = move.charAt(2) - 'a' + 1;
        int digitTo = move.charAt(3) - '0';

        Color sideFrom = field[letterFrom][digitFrom].color;
        Color sideTo = field[letterTo][digitTo].color;

        int reverse = (sideFrom == WHITE ? 1 : -1);

        if (sideTo == EMPTY) {
            boolean one = digitTo - digitFrom == reverse;
            boolean two = digitTo - digitFrom == 2 * reverse && digitFrom == (reverse == -1 ? 7 : 2);
            return one || two;
        } else {
            return Math.abs(letterTo - letterFrom) == 1 && digitTo - digitFrom == reverse && sideFrom != sideTo;
        }
    }

    private boolean rookAbility(String move) {
        int letterFrom = move.charAt(0) - 'a' + 1;
        int digitFrom = move.charAt(1) - '0';
        int letterTo = move.charAt(2) - 'a' + 1;
        int digitTo = move.charAt(3) - '0';

        Color sideFrom = field[letterFrom][digitFrom].color;
        Color sideTo = field[letterTo][digitTo].color;

        boolean rightTarget = checkTarget(sideFrom, sideTo);
        boolean straightPath = checkStraightPath(letterFrom, digitFrom, letterTo, digitTo);
        boolean cleanPath = checkClearPath(letterFrom, digitFrom, letterTo, digitTo);
        return rightTarget && straightPath && cleanPath;
    }

    private boolean nightAbility(String move) {
        int letterFrom = move.charAt(0) - 'a' + 1;
        int digitFrom = move.charAt(1) - '0';
        int letterTo = move.charAt(2) - 'a' + 1;
        int digitTo = move.charAt(3) - '0';

        Color sideFrom = field[letterFrom][digitFrom].color;
        Color sideTo = field[letterTo][digitTo].color;

        boolean rightPath = Math.abs(letterTo - letterFrom) == 2
                && Math.abs(digitTo - digitFrom) == 1
                || Math.abs(letterTo - letterFrom) == 1
                && Math.abs(digitTo - digitFrom) == 2;

        return rightPath && checkTarget(sideFrom, sideTo);
    }

    private boolean bishopAbility(String move) {
        int letterFrom = move.charAt(0) - 'a' + 1;
        int digitFrom = move.charAt(1) - '0';
        int letterTo = move.charAt(2) - 'a' + 1;
        int digitTo = move.charAt(3) - '0';

        Color sideFrom = field[letterFrom][digitFrom].color;
        Color sideTo = field[letterTo][digitTo].color;

        boolean rightTarget = (sideTo == EMPTY || sideFrom != sideTo);
        boolean sidePath = checkSidePath(letterFrom, digitFrom, letterTo, digitTo);
        boolean cleanPath = checkClearPath(letterFrom, digitFrom, letterTo, digitTo);

        return rightTarget && sidePath && cleanPath;
    }

    private boolean kingAbility(String move) {
        System.out.println("We can't check this move yet.");
        return true;
    }

    private boolean queenAbility(String move) {
        int letterFrom = move.charAt(0) - 'a' + 1;
        int digitFrom = move.charAt(1) - '0';
        int letterTo = move.charAt(2) - 'a' + 1;
        int digitTo = move.charAt(3) - '0';

        Color sideFrom = field[letterFrom][digitFrom].color;
        Color sideTo = field[letterTo][digitTo].color;

        boolean rightTarget = checkTarget(sideFrom, sideTo);
        boolean straightPath = checkStraightPath(letterFrom, digitFrom, letterTo, digitTo);
        boolean sidePath = checkSidePath(letterFrom, digitFrom, letterTo, digitTo);
        boolean cleanPath = checkClearPath(letterFrom, digitFrom, letterTo, digitTo);

        return rightTarget && (straightPath || sidePath) && cleanPath;
    }

    private boolean checkTarget(Color sideFrom, Color sideTo) {
        return sideTo == EMPTY || sideFrom != sideTo;
    }

    private boolean checkStraightPath(int letterFrom, int digitFrom, int letterTo, int digitTo) {
        return letterFrom == letterTo || digitFrom == digitTo;
    }

    private boolean checkSidePath(int letterFrom, int digitFrom, int letterTo, int digitTo) {
        return Math.abs(letterTo - letterFrom) == Math.abs(digitTo - digitFrom);
    }

    private boolean checkClearPath(int xFrom, int yFrom, int xTo, int yTo) {
        int dx = dt(xFrom, xTo);
        int dy = dt(yFrom, yTo);

        for (int x = xFrom + dx, y = yFrom + dy; x < xTo || y < yTo; x += dx, y += dy) {
            if (field[x][y].color != EMPTY) {
                return false;
            }
        }
        return true;
    }

    private int dt(int tFrom, int tTo) {
        return (tTo == tFrom ? 0 : 1) * (tTo > tFrom ? 1 : -1);
    }
}
