package handControl;

import board.details.Color;
import board.details.Move;

import java.util.ArrayList;
import java.util.Objects;

public class LichessHTMLParser {

    public static Color getOurColor(String source, String nickname) {
        String WHITE_COLOR_INFO = "\"player\":{\"color\":\"white\",\"user\":{\"id\":\"%s";
        if (source.contains(String.format(WHITE_COLOR_INFO, nickname))) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    public static boolean isGameFinished(String source) {
        String statusObject = JSONParser.parseField("status", source, true);
        String status = JSONParser.parseField("name", statusObject, false);
        return !Objects.equals(status, "started");
    }

    public static String getLastMove(String source) {
        return JSONParser.parseField("lastMove", source, false);
    }

    public static ArrayList<Move> getAllMoves(String source) {
        return JSONParser.parseMoveArray(source);
    }
}
