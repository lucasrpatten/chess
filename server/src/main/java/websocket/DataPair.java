package websocket;

import org.eclipse.jetty.websocket.api.Session;

import model.GameData;

public class DataPair {
    private final Session session;
    private final GameData gameData;

    public DataPair(Session session, GameData gameData) {
        this.session = session;
        this.gameData = gameData;
    }

    public Session getSession() {
        return session;
    }

    public GameData getGameData() {
        return gameData;
    }
}