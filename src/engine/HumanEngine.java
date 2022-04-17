package engine;

import board.*;
import board.details.Move;

import java.util.Scanner;

public class HumanEngine implements Engine {

    @Override
    public Move nextMove(Board board) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            board.showBoard();
            System.out.println("Insert your move for " + board.turn().name());
            Move move = new Move(scanner.next());
            if (!board.correctMove(move)) {
                System.err.println("Move is incorrect.");
            } else {
                System.out.println("Move is correct.");
                return move;
            }
        }
    }
}
