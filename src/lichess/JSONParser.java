package lichess;

public class JSONParser {
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
