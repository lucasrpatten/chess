package websocket;

import org.eclipse.jetty.websocket.api.Session;

import model.AuthData;
import model.GameData;

public class DataPair {
    private final AuthData authData;
    private final GameData gameData;

    public DataPair(AuthData authData, GameData gameData) {
        this.authData = authData;
        this.gameData = gameData;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public GameData getGameData() {
        return gameData;
    }
}