package engine;

import board.*;

import java.util.Scanner;

public class HumanEngine implements Engine {

    @Override
    public String nextMove(Board board) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            board.showBoard();
            System.out.println("Insert your move for " + board.turn().name());
            String move = scanner.next();
            if (!board.correctMove(move)) {
                System.err.println("Move is incorrect.");
            } else {
                System.out.println("Move is correct.");
                return move;
            }
        }
    }
}
