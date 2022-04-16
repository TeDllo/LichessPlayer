package board;

public class Cell {
    public Figure figure;
    public Color color;

    public boolean moved = false;

    @Override
    public String toString() {
        return " " + switch (color) {
            case WHITE -> Character.toString(figure.name().charAt(0));
            case BLACK -> Character.toString(figure.name().toLowerCase().charAt(0));
            default -> " ";
        } + " ";
    }
}
