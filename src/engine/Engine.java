package engine;

import board.Board;
import board.details.Move;

public interface Engine {

    Move nextMove(Board board);

}
