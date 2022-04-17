package board.details;

public class Cell {
    public Figure figure;
    public Color color;

    public boolean moved = false;

    @Override
    public String toString() {
        return " "
                + (color == Color.EMPTY
                ? " "
                : Character.toString(figure.name().charAt(0)))
                + " ";
    }
}
