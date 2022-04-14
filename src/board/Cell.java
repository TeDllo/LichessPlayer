package board;

public class Cell {
    public Figure figure;
    public Color color;

    @Override
    public String toString() {
        switch (color) {
            case WHITE:
                return Character.toString(figure.name().charAt(0));
            case BLACK:
                return Character.toString(figure.name().toLowerCase().charAt(0));
        }
        return ".";
    }
}
