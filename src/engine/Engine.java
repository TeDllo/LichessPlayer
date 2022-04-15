package engine;

import board.Board;
import board.Move;

public interface Engine {

    Move nextMove(Board board);

}
