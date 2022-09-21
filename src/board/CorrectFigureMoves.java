package board;

import board.details.Color;
import board.details.Point;

import java.util.ArrayList;

import static board.details.Color.EMPTY;
import static board.details.Color.WHITE;

public class CorrectFigureMoves {

    private static final int[] side = new int[]{-1, 1};
    private static final int[] nightDelta = new int[]{1, 2};

    private static boolean correctPoint(Point point) {
        return 1 <= point.letter && point.letter <= 8
                && 1 <= point.digit && point.digit <= 8;
    }

    private static ArrayList<Point> correctMoves(Point[] points) {
        ArrayList<Point> result = new ArrayList<>();

        for (Point point : points) {
            if (correctPoint(point)) {
                result.add(point);
            }
        }

        return result;
    }

    private static void addLines(ArrayList<Point> points, Point point) {
        for (int letter = 1; letter < 9; letter++) {
            if (letter != point.letter) {
                points.add(new Point(letter, point.digit));
            }
        }
        for (int digit = 1; digit < 9; digit++) {
            if (digit != point.digit) {
                points.add(new Point(point.letter, digit));
            }
        }
    }

    private static void addSides(ArrayList<Point> points, Point point) {
        for (int delta = 1; delta < 8; delta++) {
            for (int s1 : side) {
                for (int s2 : side) {
                    Point p = new Point(point.letter + s1 * delta, point.digit + s2 * delta);
                    if (correctPoint(p)) {
                        points.add(p);
                    }
                }
            }
        }
    }

    public static ArrayList<Point> pawnMoves(Point point, Color turn) {
        int reverse = (turn == WHITE ? 1 : -1);

        Point[] points = new Point[]{
                new Point(point.letter, point.digit + reverse),
                new Point(point.letter, point.digit + 2 * reverse),
                new Point(point.letter + 1, point.digit + reverse),
                new Point(point.letter - 1, point.digit + reverse)
        };

        return correctMoves(points);
    }

    public static ArrayList<Point> kingMoves(Point point) {
        Point[] points = new Point[]{
                new Point(point.letter + 1, point.digit + 1),
                new Point(point.letter, point.digit + 1),
                new Point(point.letter - 1, point.digit + 1),
                new Point(point.letter + 1, point.digit),
                new Point(point.letter - 1, point.digit),
                new Point(point.letter + 1, point.digit - 1),
                new Point(point.letter, point.digit - 1),
                new Point(point.letter - 1, point.digit - 1),
                new Point(point.letter + 2, point.digit),
                new Point(point.letter - 2, point.digit)
        };

        return correctMoves(points);
    }

    public static ArrayList<Point> rookMoves(Point point) {
        ArrayList<Point> points = new ArrayList<>();
        addLines(points, point);
        return points;
    }

    public static ArrayList<Point> bishopMoves(Point point) {
        ArrayList<Point> points = new ArrayList<>();
        addSides(points, point);
        return points;
    }

    public static ArrayList<Point> queenMoves(Point point) {
        ArrayList<Point> points = new ArrayList<>();
        addLines(points, point);
        addSides(points, point);
        return points;
    }

    public static ArrayList<Point> nightMoves(Point point) {
        ArrayList<Point> points = new ArrayList<>();
        for (int s1 : side) {
            for (int s2 : side) {
                for (int delta : nightDelta) {
                    Point p = new Point(point.letter + delta * s1, point.digit + (3 - delta) * s2);
                    if (correctPoint(p)) {
                        points.add(p);
                    }
                }
            }
        }
        return points;
    }

}
