package engine;

import alarm.WinningMessage;
import board.*;
import board.details.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class HalfRandomEngine implements Engine {

    private ArrayList<Integer> lastPositions;

    private static final int INFINITY_BALANCE = 9999;
    private static final int FIGURE_COEFFICIENT = 3;
    private static final int POSITION_COEFFICIENT = 10;

    private static final Map<Figure, Integer> COST = Map.of(
            Figure.KING, 0,
            Figure.QUEEN, 900,
            Figure.ROOK, 500,
            Figure.BISHOP, 345,
            Figure.NIGHT, 285,
            Figure.PAWN, 100
    );

    private Map<String, Integer> history;
    private int counterGot = 0;
    private int counter = 0;

    private int DEPTH;

    private Color ourColor = Color.WHITE;

    public HalfRandomEngine(int depth) {
        this.DEPTH = depth;
        lastPositions = new ArrayList<>();
    }

    @Override
    public Move nextMove(Board board) {

        if (figureBalance(board) > 1000) {
            Thread thread = new Thread(new WinningMessage());
            thread.start();
        }

        System.out.println("Started Calculating:");
        board.showBoard();

        counter = 0;
        ourColor = board.turn();

        ArrayList<Move> correctMoves = correctSideMoves(board);

        int bestMoveBalance = -INFINITY_BALANCE;
        Move bestMove = new Move("a1a1");

        for (Move move : correctMoves) {
            Cell eater = board.getCell(move.letFrom, move.digFrom);
            Cell food = board.getCell(move.letTo, move.digTo);
            boolean castling = board.isCastling(move);
            board.makeMove(move);

            if (board.isMate(board.turn())) {
                bestMove = move;
                board.revertMove(move, eater, food, castling);
                break;
            }

            int value = minimax(board, DEPTH, -INFINITY_BALANCE, INFINITY_BALANCE, false);
            board.revertMove(move, eater, food, castling);

            if (value > bestMoveBalance) {
                bestMoveBalance = value;
                bestMove = move;
            }

            System.out.println("Move " + move.text + " balance " + value);
        }
        System.out.println("Finished Calculating:");
        board.showBoard();
        System.out.println("Best move: " + bestMove.text);
        System.out.println(counter + " moves calculated.");

        lastPositions.add(counter);
        if (shouldDeeper()) {
            DEPTH = 4;
        }

        return bestMove;
    }

    private boolean shouldDeeper() {
        if (lastPositions.size() >= 4) {
            for (int i = lastPositions.size() - 4; i < lastPositions.size(); i++) {
                if (lastPositions.get(i) > 20000) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean isMaximising) {
        counter++;
        if (depth == 0) {
            return calculateBalance(board);
        }

        int bestMove = INFINITY_BALANCE * (isMaximising ? -1 : 1);

        if (board.isMate(board.turn())) {
            return (INFINITY_BALANCE - 1) * (isMaximising ? -1 : 1);
        }

        ArrayList<Move> correctMoves = correctSideMoves(board);

        if (correctMoves.size() == 0) {
            return 0;
        }

        BiFunction<Integer, Integer, Integer> minMax = (isMaximising ? Math::max : Math::min);

        for (Move move : correctMoves) {
            Cell eater = board.getCell(move.letFrom, move.digFrom);
            Cell food = board.getCell(move.letTo, move.digTo);

            boolean castling = board.isCastling(move);
            board.makeMove(move);

            int value = minimax(board, depth - 1, alpha, beta, !isMaximising);
            if (board.isCastling(move)) {
                value += 100;
            }

            board.revertMove(move, eater, food, castling);

            bestMove = minMax.apply(bestMove, value);

            if (isMaximising) {
                alpha = minMax.apply(alpha, bestMove);
            } else {
                beta = minMax.apply(beta, bestMove);
            }
            if (beta <= alpha) {
                return bestMove;
            }
        }
        return bestMove;
    }

    private ArrayList<Move> correctSideMoves(Board board) {
        ArrayList<Move> result = new ArrayList<>();
        Cell[][] field = board.getField();

        for (int letter = 1; letter < 9; letter++) {
            for (int digit = 1; digit < 9; digit++) {
                if (field[letter][digit].color == board.turn()) {
                    result.addAll(correctFigureMoves(board, new Point(letter, digit)));
                }
            }
        }

        return result;
    }

    private ArrayList<Move> correctFigureMoves(Board board, Point from) {
        ArrayList<Move> result = new ArrayList<>();
        ArrayList<Point> points
                = board.getPossibleMoves(board.getCell(from.letter, from.digit).figure, from);

        for (Point point : points) {
            Move move = new Move(from.letter, from.digit, point.letter, point.digit);
            if (board.correctMove(move)) {
                result.add(move);
            }
        }

        return result;
    }

    public int calculateBalance(Board board) {
        return figureBalance(board) * FIGURE_COEFFICIENT + positionBalance(board) * POSITION_COEFFICIENT;
    }

    private int figureBalance(Board board) {
        int balance = 0;

        for (int letter = 1; letter < 9; letter++) {
            for (int digit = 1; digit < 9; digit++) {
                Cell cell = board.getField()[letter][digit];
                if (cell.color != Color.EMPTY) {
                    balance += COST.get(cell.figure) * (cell.color == ourColor ? 1 : -1);
                }
            }
        }

        return balance;
    }

    private int positionBalance(Board board) {
        int balance = 0;

        Cell[][] field = board.getField();

        for (int letter = 1; letter < 9; letter++) {
            for (int digit = 1; digit < 9; digit++) {
                Color color = field[letter][digit].color;
                if (color != Color.EMPTY) {
                    balance += getPositionCoefficient(letter - 1, digit - 1, field[letter][digit].figure, color)
                            * (color == ourColor ? 1 : -1);
                }
            }
        }

        return balance;
    }

    private static int getPositionCoefficient(int letter, int digit, Figure figure, Color color) {
        letter = (color == Color.WHITE ? letter : 7 - letter);
        digit = (color == Color.WHITE ? digit : 7 - digit);

        return switch (figure) {
            case KING -> KING_PLACE[letter][digit];
            case QUEEN -> QUEEN_PLACE[letter][digit];
            case ROOK -> ROOK_PLACE[letter][digit];
            case BISHOP -> BISHOP_PLACE[letter][digit];
            case NIGHT -> KNIGHT_PLACE[letter][digit];
            case PAWN -> PAWN_PLACE[letter][digit];
        };
    }

    private static final int[][] KING_PLACE = new int[][]{
            {20, 20, -10, -20, -30, -30, -30, -30},
            {30, 20, -20, -30, -40, -40, -40, -40},
            {10, 0, -20, -30, -40, -40, -40, -40},
            {0, 0, -20, -40, -50, -50, -50, -50},
            {0, 0, -20, -40, -50, -50, -50, -50},
            {10, 0, -20, -30, -40, -40, -40, -40},
            {30, 20, -20, -30, -40, -40, -40, -40},
            {20, 20, -10, -20, -30, -30, -30, -30}
    };

    private static final int[][] QUEEN_PLACE = new int[][]{
            {-20, -20, -10, 0, -5, -10, -10, -20},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-5, 0, 5, 5, -5, 5, 0, -5},
            {-5, 0, 5, 5, -5, 5, 0, -5},
            {-10, 5, 5, 5, 5, 5, 0, -10},
            {-10, 0, 5, 0, 0, 0, 0, -10},
            {-20, -20, -10, 0, -5, -10, -10, -20}
    };

    private static final int[][] ROOK_PLACE = new int[][]{
            {0, -5, -5, -5, -5, -5, 5, 0},
            {0, 0, 0, 0, 0, 0, 10, 0},
            {0, 0, 0, 0, 0, 0, 10, 0},
            {0, 5, 0, 0, 0, 0, 10, 0},
            {0, 5, 0, 0, 0, 0, 10, 0},
            {0, 0, 0, 0, 0, 0, 10, 0},
            {0, 0, 0, 0, 0, 0, 10, 0},
            {0, -5, -5, -5, -5, -5, 5, 0}
    };

    private static final int[][] BISHOP_PLACE = new int[][]{
            {-20, -10, -10, -10, -10, -10, -10, -20},
            {-10, 5, 10, 0, 5, 0, 0, -10},
            {-10, 0, 10, 10, 5, 5, 0, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 0, 10, 10, 10, 10, 0, -10},
            {-10, 0, 10, 10, 5, 5, 0, -10},
            {-10, 5, 10, 0, 5, 0, 0, -10},
            {-20, -10, -10, -10, -10, -10, -10, -20}
    };

    private static final int[][] KNIGHT_PLACE = new int[][]{
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20, 5, 0, 5, 0, -20, -40},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-30, 5, 15, 20, 20, 15, 0, -30},
            {-30, 5, 15, 20, 20, 15, 0, -30},
            {-30, 0, 10, 15, 15, 10, 0, -30},
            {-40, -20, 5, 0, 5, 0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50}
    };

    private static final int[][] PAWN_PLACE = new int[][]{
            {0, 5, 5, 0, 5, 10, 5, 0},
            {0, 10, -5, 0, 5, 10, 5, 0},
            {0, 10, -10, 0, 10, 20, 5, 0},
            {0, -20, 0, 20, 25, 30, 5, 0},
            {0, -20, 0, 20, 25, 30, 5, 0},
            {0, 10, -10, 0, 10, 20, 5, 0},
            {0, 10, -5, 0, 5, 10, 5, 0},
            {0, 5, 5, 0, 5, 10, 5, 0}
    };

}


