package web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;

import chess.ChessGame;
import model.AuthData;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.EmptyRequest;
import model.GameData;
import model.GameListResult;
import model.JoinGameRequest;
import model.LoginRequest;
import model.UserData;
import ui.Data;
import ui.PostloginUI;

public class ServerFacade {
    private final String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    public AuthData register(UserData user) {
        AuthData authData = request("/user", "POST", user, AuthData.class);
        Data.getInstance().setAuthToken(authData.authToken());
        Data.getInstance().setUsername(user.username());
        Data.getInstance().setState(Data.State.LOGGED_IN);
        return authData;
    }

    public AuthData login(LoginRequest loginRequest) {
        AuthData authData = request("/session", "POST", loginRequest, AuthData.class);
        Data.getInstance().setAuthToken(authData.authToken());
        Data.getInstance().setUsername(authData.username());
        Data.getInstance().setState(Data.State.LOGGED_IN);
        return authData;
    }

    public void logout() {
        request("/session", "DELETE", null, EmptyRequest.class);
        Data.getInstance().setAuthToken(null);
        Data.getInstance().setUsername(null);
        Data.getInstance().setState(Data.State.LOGGED_OUT);
        return;
    }

    public CreateGameResult createGame(CreateGameRequest createReq) {
        return request("/game", "POST", createReq, CreateGameResult.class);
    }

    public List<GameData> listGames() {
        Comparator<GameData> comparator = new Comparator<GameData>() {
            @Override
            public int compare(GameData o1, GameData o2) {
                int emptyo1 = PostloginUI.emptySpots(o1);
                int emptyo2 = PostloginUI.emptySpots(o2);
                if (emptyo1 != emptyo2) {
                    return Integer.compare(emptyo1, emptyo2);
                }

                return Integer.compare(o1.gameID(), o2.gameID());
            }
        };

        GameListResult result = request("/game", "GET", null, GameListResult.class);
        List<GameData> games = new ArrayList<>(result.games());
        games.sort(comparator);
        Data.getInstance().setGameList(games);
        return games;
    }

    public void joinGame(ChessGame.TeamColor color, int gameNumber) {
        int gameID = Data.getInstance().getGameList().get(gameNumber - 1).gameID();
        JoinGameRequest joinReq = new JoinGameRequest(color, gameID);
        request("/game", "PUT", joinReq, EmptyRequest.class);
        Data.getInstance().addGameID(gameID);
        Data.getInstance().setGameNumber(gameNumber);
        Data.getInstance().setState(Data.State.IN_GAME);
        return;
    }

    public void observeGame(int gameNumber) {
        int gameID = Data.getInstance().getGameList().get(gameNumber - 1).gameID();
        Data.getInstance().addGameID(gameID);
        Data.getInstance().setGameNumber(gameNumber);
        Data.getInstance().setState(Data.State.IN_GAME);
        return;
    }

    private <T> T request(String endpointUrl, String method, Object request, Class<T> responseType) {
        try {
            Gson gson = new Gson();

            URL url = new URI(this.url).resolve(endpointUrl).toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.toUpperCase());
            connection.addRequestProperty("Accept", "application/json");
            connection.setDoOutput(request != null);

            String authToken = Data.getInstance().getAuthToken();
            if (authToken != null) {
                connection.addRequestProperty("Authorization", authToken);
            }

            connection.connect();

            if (request != null) {
                OutputStream os = connection.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
                writer.write(gson.toJson(request));
                writer.flush();
                os.close();
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed: " + connection.getResponseMessage());
            }

            String response = new String(connection.getInputStream().readAllBytes(), "UTF-8");
            if (responseType != null) {
                return gson.fromJson(response, responseType);
            }
            return null;
        }
        catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Error connecting to server: " + e.getMessage());
        }
    }
}
