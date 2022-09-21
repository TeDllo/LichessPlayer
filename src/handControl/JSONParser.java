package handControl;

import board.details.Move;

import java.util.ArrayList;

public class JSONParser {


    public static ArrayList<Move> parseMoveArray(String body) {
        ArrayList<Move> array = new ArrayList<>();
        try {
            int nextStart = body.indexOf("\"ply\":1");
            while (nextStart != -1) {
                int uciIndex = body.indexOf("uci", nextStart);
                String moveText = body.substring(uciIndex + 6, uciIndex + 10);

                String san = parseField("san", body.substring(uciIndex), false);
                assert san != null;
                if (san.equals("O-O") || san.equals("O-O+")) {
                    System.out.println("O-O: " + moveText);
                    moveText = moveText.substring(0, 2) + "g" + moveText.charAt(3);
                }

                if (san.equals("O-O-O") || san.equals("O-O-O+")) {
                    System.out.println("O-O-O: " + moveText);
                    moveText = moveText.substring(0, 2) + "c" + moveText.charAt(3);
                }

                array.add(new Move(moveText));
                nextStart = body.indexOf("\"ply\":" + (array.size() + 1));
            }
            return array;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return array;
        }
    }

    public static String parseField(String field, String body, boolean isObject) {
        try {
            String end = (isObject ? "}" : "\"");
            int start = body.indexOf(field) + field.length() + 3;
            int stop = body.indexOf(end, start);
            return body.substring(start, stop);
        } catch (Exception e) {
            return null;
        }
    }
}
