package board;

import board.details.Cell;
import board.details.Color;
import color.ConsoleColors;

import static board.details.Color.WHITE;

public class BoardPainter {

    private static final int WHITE_REVERSE = 0;
    private static final int BLACK_REVERSE = 9;

    public static void paintBoard(Board board) {
        printTurn(board.turn());
        printField(board.getField(), (board.turn() == WHITE ? WHITE_REVERSE : BLACK_REVERSE));
    }

    private static void printTurn(Color turn) {
        System.out.printf("\t\t  %s's turn\n", turn.name());
    }

    private static void printField(Cell[][] field, int reverse) {
        for (int digit = 8; digit > 0; digit--) {
            System.out.print(Math.abs(reverse - digit) + " | ");
            for (int letter = 1; letter < 9; letter++) {
                Cell cell = field[Math.abs(reverse - letter)][Math.abs(reverse - digit)];
                setMode(getBackgroundColor(letter, digit), getTextColor(cell.color));
                System.out.print(" " + cell + " " + ConsoleColors.RESET);
            }
            System.out.println();
        }
        printFooter(reverse);
    }

    private static String getBackgroundColor(int letter, int digit) {
        return ((digit + letter) % 2 == 1 ? ConsoleColors.PURPLE_BACKGROUND : ConsoleColors.CYAN_BACKGROUND);
    }

    private static String getTextColor(Color color) {
        return (color == WHITE ? ConsoleColors.WHITE_BOLD_BRIGHT : ConsoleColors.BLACK_BOLD);
    }

    private static void setMode(String backgroundMode, String textMode) {
        System.out.print(backgroundMode + textMode);
    }

    private static void printFooter(int reverse) {
        System.out.print("--|-------------------------\n  | ");
        for (int letter = 1; letter < 9; letter++) {
            System.out.print(" " + Character.toString(Math.abs(reverse - letter) + 'a' - 1) + " ");
        }
        System.out.println();
    }
}
