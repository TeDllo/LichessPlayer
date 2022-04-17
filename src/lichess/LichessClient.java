package lichess;

import board.exceptions.BadMoveException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LichessClient {

    private static final String LICHESS = "https://lichess.org";

    private final URL server;
    private final String access_token;
    private final boolean log;

    public LichessClient(String access_token, boolean log) throws MalformedURLException {
        this.log = log;
        this.server = new URL(LICHESS);
        this.access_token = access_token;
    }

    public InputStream getStreamRequest(String gameID) throws IOException {
        return get("/api/board/game/stream/" + gameID);
    }

    public void makeMoveRequest(String gameID, String move) throws IOException {
        post("/api/board/game/" + gameID + "/move/" + move);
    }

    // -------------------private--------------------

    private InputStream get(String path) throws IOException {
        HttpURLConnection urlConnection = openConnection(path);
        handleResponse("GET", urlConnection);
        return urlConnection.getInputStream();
    }

    private void post(String path) throws IOException {
        HttpURLConnection urlConnection = openConnection(path);
        urlConnection.setRequestMethod("POST");
        handleResponse("POST", urlConnection);

    }

    private HttpURLConnection openConnection(String path) throws IOException {
        URL url = new URL(server, path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.addRequestProperty("Authorization", "Bearer " + access_token);
        return urlConnection;
    }

    private void handleResponse(String type, HttpURLConnection urlConnection) throws IOException {

        int code = urlConnection.getResponseCode();
        String message = urlConnection.getResponseMessage();

        if (log) {
            System.out.println("Making " + type + " request:");
            System.out.println(code + " " + message);
        }

        if (code != 200) {
            InputStream in = urlConnection.getErrorStream();
            String msg = extractInputStreamData(in);
            if (msg.contains("move")) {
                System.err.println(msg);
                throw new BadMoveException();
            }
        }
    }

    public String extractInputStreamData(InputStream in) throws IOException {
        return new BufferedReader(new InputStreamReader(in,
                StandardCharsets.UTF_8)).readLine();
    }
}
