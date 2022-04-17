package board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import static board.details.Figure.*;
import static board.details.Color.*;

import board.details.Cell;
import board.details.Color;
import board.details.Figure;
import board.details.Move;

public class ClassicBoard implements Board {

    private Color ourColor;
    private Cell[][] field;
    private ArrayList<Move> moves;

    private static final Function<String, Move> mapper = (Move::new);

    public ClassicBoard() {
        moves = new ArrayList<>();
        initField();
    }

    @Override
    public void insertMoves(String moves) {
        if (!moves.isEmpty()) {
            this.moves = Arrays.stream(moves.split(" "))
                    .map(mapper)
                    .collect(Collectors.toCollection(ArrayList::new));
            updateField();
        }
    }

    @Override
    public void appendMove(Move move) {
        moves.add(move);
        makeMove(move);
    }

    @Override
    public Cell[][] getField() {
        return field;
    }

    @Override
    public void showBoard() {
        BoardPainter.paintBoard(this);
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
    public boolean correctMove(Move move) {
        boolean isMove = (move.letFrom != move.letTo || move.digFrom != move.digTo);
        boolean hasFigure = field[move.letFrom][move.digFrom].color != EMPTY;
        boolean hasAbility = checkAbility(move);
        boolean hasRights = checkRights(move);

        return isMove && hasFigure && hasAbility && hasRights;
    }

    @Override
    public boolean isCheck(Color side) {
        int kingLet = 1;
        int kingDig = 1;

        for (int let = 1; let < 9; let++) {
            for (int dig = 1; dig < 9; dig++) {
                if (field[let][dig].figure == KING
                        && field[let][dig].color == side) {
                    kingLet = let;
                    kingDig = dig;
                    break;
                }
            }
        }

        Color enemy = (side == WHITE ? BLACK : WHITE);

        for (int let = 1; let < 9; let++) {
            for (int dig = 1; dig < 9; dig++) {
                if (field[let][dig].color == enemy
                        && checkAbility(new Move(let, dig, kingLet, kingDig))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isMate() {
        return false;
    }

    private void updateField() {
        initField();
        for (Move move : moves) {
            makeMove(move);
        }
    }

    private void makeMove(Move move) {

        // Checking Castling
        if (field[move.letFrom][move.digFrom].figure == KING && kingCastling(move, field[move.letFrom][move.digFrom].color)) {
            int rookLetFrom = (move.letTo == 3 ? 1 : 8);
            int rookDigFrom = (move.digTo);
            int rookLetTo = (move.letTo == 3 ? 4 : 6);
            int rookDigTo = (move.digTo);
            makeMove(new Move(rookLetFrom, rookDigFrom, rookLetTo, rookDigTo));
        }

        field[move.letTo][move.digTo].figure = field[move.letFrom][move.digFrom].figure;
        field[move.letTo][move.digTo].color = field[move.letFrom][move.digFrom].color;

        field[move.letFrom][move.digFrom].color = EMPTY;

        // Checking transforming into QUEEN
        if (field[move.letTo][move.digTo].figure == PAWN) {
            int line = field[move.letTo][move.digTo].color == WHITE ? 8 : 1;
            if (move.digTo == line) {
                field[move.letTo][move.digTo].figure = QUEEN;
            }
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
                            case 1, 8 -> field[letter][digit].figure = ROOK;
                            case 2, 7 -> field[letter][digit].figure = NIGHT;
                            case 3, 6 -> field[letter][digit].figure = BISHOP;
                            case 4 -> field[letter][digit].figure = QUEEN;
                            case 5 -> field[letter][digit].figure = KING;
                        }
                    }

                    field[letter][digit].color = (digit <= 2 ? WHITE : BLACK);
                }
            }
        }
    }

    private boolean checkRights(Move move) {
        ClassicBoard board = new ClassicBoard();
        for (Move initMove : moves) {
            board.appendMove(initMove);
        }
        board.appendMove(move);
        return !board.isCheck(field[move.letFrom][move.digFrom].color);
    }

    private boolean checkAbility(Move move) {
        Figure figure = field[move.letFrom][move.digFrom].figure;
        return switch (figure) {
            case KING -> kingAbility(move);
            case QUEEN -> queenAbility(move);
            case BISHOP -> bishopAbility(move);
            case NIGHT -> nightAbility(move);
            case ROOK -> rookAbility(move);
            default -> pawnAbility(move);
        };
    }

    private boolean pawnAbility(Move move) {
        Color sideFrom = field[move.letFrom][move.digFrom].color;
        Color sideTo = field[move.letTo][move.digTo].color;

        int reverse = (sideFrom == WHITE ? 1 : -1);

        if (sideTo == EMPTY) {
            boolean one = move.digTo - move.digFrom == reverse;
            boolean two = move.digTo - move.digFrom == 2 * reverse && move.digFrom == (reverse == -1 ? 7 : 2);
            return one || two;
        } else {
            return Math.abs(move.letTo - move.letFrom) == 1 && move.digTo - move.digFrom == reverse && sideFrom != sideTo;
        }
    }

    private boolean rookAbility(Move move) {
        Color sideFrom = field[move.letFrom][move.digFrom].color;
        Color sideTo = field[move.letTo][move.digTo].color;

        boolean rightTarget = checkTarget(sideFrom, sideTo);
        boolean straightPath = checkStraightPath(move);
        boolean cleanPath = checkClearPath(move);
        return rightTarget && straightPath && cleanPath;
    }

    private boolean nightAbility(Move move) {
        Color sideFrom = field[move.letFrom][move.digFrom].color;
        Color sideTo = field[move.letTo][move.digTo].color;

        int dLet = Math.abs(move.letTo - move.letFrom);
        int dDig = Math.abs(move.digTo - move.digFrom);

        boolean rightPath = (dLet == 2 && dDig == 1) || (dLet == 1 && dDig == 2);

        return rightPath && checkTarget(sideFrom, sideTo);
    }

    private boolean bishopAbility(Move move) {
        Color sideFrom = field[move.letFrom][move.digFrom].color;
        Color sideTo = field[move.letTo][move.digTo].color;

        boolean rightTarget = checkTarget(sideFrom, sideTo);
        boolean sidePath = checkSidePath(move);
        boolean cleanPath = checkClearPath(move);

        return rightTarget && sidePath && cleanPath;
    }

    private boolean kingAbility(Move move) {
        Color sideFrom = field[move.letFrom][move.digFrom].color;
        Color sideTo = field[move.letTo][move.digTo].color;

        boolean target = checkTarget(sideFrom, sideTo);
        boolean stepLet = Math.abs(move.letFrom - move.letTo) <= 1;
        boolean stepDig = Math.abs(move.digFrom - move.digTo) <= 1;

        return target && stepLet && stepDig || kingCastling(move, sideFrom);
    }

    private boolean kingCastling(Move move, Color side) {
        String[] moves = (side == WHITE
                ? new String[]{"e1c1", "e1g1"}
                : new String[]{"e8c8", "e8g8"});
        int rookLet = (move.letTo == 3 ? 1 : 8);

        boolean rightMove = move.text.equals(moves[0]) || move.text.equals(moves[1]);
        boolean isRook = field[rookLet][move.digFrom].figure == ROOK;
        boolean isMoved = field[rookLet][move.digFrom].moved;
        boolean cleanPath = checkClearPath(new Move(move.letFrom, move.digFrom, rookLet, move.digFrom));

        return rightMove && isRook && !isMoved && cleanPath && !isCheck(side);
    }

    private boolean queenAbility(Move move) {
        Color sideFrom = field[move.letFrom][move.digFrom].color;
        Color sideTo = field[move.letTo][move.digTo].color;

        boolean rightTarget = checkTarget(sideFrom, sideTo);
        boolean straightPath = checkStraightPath(move);
        boolean sidePath = checkSidePath(move);
        boolean cleanPath = checkClearPath(move);

        return rightTarget && (straightPath || sidePath) && cleanPath;
    }

    private boolean checkTarget(Color sideFrom, Color sideTo) {
        return sideTo == EMPTY || sideFrom != sideTo;
    }

    private boolean checkStraightPath(Move move) {
        return move.letFrom == move.letTo || move.digFrom == move.digTo;
    }

    private boolean checkSidePath(Move move) {
        return Math.abs(move.letTo - move.letFrom) == Math.abs(move.digTo - move.digFrom);
    }

    private boolean checkClearPath(Move move) {

        boolean straight = checkStraightPath(move);
        boolean side = checkSidePath(move);

        if (!straight && !side) {
            return false;
        }

        int dx = dt(move.letFrom, move.letTo);
        int dy = dt(move.digFrom, move.digTo);

        for (int x = move.letFrom + dx, y = move.digFrom + dy; x != move.letTo || y != move.digTo; x += dx, y += dy) {
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
