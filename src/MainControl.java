import engine.HalfRandomEngine;
import handControl.HandImitator;
import lichess.LichessClient;
import lichess.Tournament;


public class MainControl {

    public static String access_token = "TOKEN";
    public static String nickname = "tedllo";

    public static void main(String... args) throws Exception {
        LichessClient client = new LichessClient(access_token, false);
        HalfRandomEngine engine = new HalfRandomEngine(3);

        Tournament tournament = new Tournament(nickname, client, engine, new HandImitator());
        tournament.start();
    }
}