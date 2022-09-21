package lichess;

import board.Board;
import board.ClassicBoard;
import engine.Engine;
import game.Game;
import game.HandGame;
import game.LichessAPIGame;
import handControl.HandImitator;

import static handControl.JSONParser.parseField;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Tournament {

    private final String nickname;
    private final LichessClient client;
    private final Engine engine;
    private final HandImitator imitator;

    public Tournament(String nickname, LichessClient client, Engine engine, HandImitator imitator) {
        this.nickname = nickname.toLowerCase();
        this.client = client;
        this.engine = engine;
        this.imitator = imitator;
    }

    public void start() throws IOException, InterruptedException, AWTException {
        InputStream in = client.getStreamEventsRequest();
        String eventJSON = client.extractInputStreamData(in);
        while (eventJSON != null) {
            if (!eventJSON.equals("")) {
                System.out.println(eventJSON);
                processEvent(eventJSON);
            }
            eventJSON = client.extractInputStreamData(in);
        }
    }

    private void processEvent(String eventJSON) throws InterruptedException, AWTException {
        if (Objects.equals(parseField("type", eventJSON, false), "gameStart")) {
            String gameObject = parseField("game", eventJSON, true);
            String gameID = parseField("fullId", gameObject, false);

            Board board = new ClassicBoard();
            Game game = new HandGame(nickname, board, engine, imitator, gameID);
            game.start();
        }
    }
}
