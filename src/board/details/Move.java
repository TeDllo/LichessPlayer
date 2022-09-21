package board.details;

public class Move implements Comparable<Move> {
    public final int letFrom;

    public final int digFrom;
    public final int letTo;
    public final int digTo;
    public final String text;

    public int balance;

    public Move(String move) {
        letFrom = move.charAt(0) - 'a' + 1;
        digFrom = move.charAt(1) - '0';
        letTo = move.charAt(2) - 'a' + 1;
        digTo = move.charAt(3) - '0';

        text = move;
    }

    public Move(int x1, int y1, int x2, int y2) {
        letFrom = x1;
        digFrom = y1;
        letTo = x2;
        digTo = y2;

        text = String.format("%c%c%c%c", letFrom + 'a' - 1, digFrom + '0', letTo + 'a' - 1, digTo + '0');
    }

    @Override
    public int compareTo(Move o) {
        if (this.balance < o.balance) {
            return -1;
        } else if (this.balance == o.balance) {
            return 0;
        }
        return 1;
    }
}
