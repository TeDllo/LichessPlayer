package board.details;

public class Cell {
    public Figure figure;
    public Color color;

    public boolean moved = false;

    @Override
    public String toString() {
        return (color == Color.EMPTY
                ? " "
                : Character.toString(figure.name().charAt(0)));
    }

    public Cell() {
    }

    public Cell(Cell cell) {
        this.figure = cell.figure;
        this.color = cell.color;
        this.moved = cell.moved;
    }
}
