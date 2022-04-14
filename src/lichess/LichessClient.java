package lichess;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    public InputStream streamRequest(String gameID) throws IOException {
        return get("/api/board/game/stream/" + gameID);
    }

    public void makeMoveRequest(String gameID, String move) throws IOException {
        post("/api/board/game/" + gameID + "/move/" + move);
    }

    // -------------------private--------------------

    private InputStream get(String path) throws IOException {
        HttpURLConnection urlConnection = openConnection(path);
        if (log) {
            debugRequest("GET", urlConnection);
        }
        return urlConnection.getInputStream();
    }

    private void post(String path) throws IOException {
        HttpURLConnection urlConnection = openConnection(path);
        urlConnection.setRequestMethod("POST");
        if (log) {
            debugRequest("POST", urlConnection);
        }
    }

    private HttpURLConnection openConnection(String path) throws IOException {
        URL url = new URL(server, path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.addRequestProperty("Authorization", "Bearer " + access_token);
        return urlConnection;
    }

    private static void debugRequest(String type, HttpURLConnection urlConnection) throws IOException {

        System.out.println("Making " + type + " request:");
        System.out.println(urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());

    }
}
