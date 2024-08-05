package ui;

import java.util.ArrayList;
import java.util.List;

import model.GameData;
import web.ServerFacade;

public class Data {
    public enum State {
        LOGGED_OUT, LOGGED_IN, IN_GAME
    }

    private static Data instance = new Data();

    public static Data getInstance() {
        return instance;
    }

    public static void setInstance(Data instance) {
        Data.instance = instance;
    }

    private ServerFacade serverFacade;

    private String authToken;

    private String username;

    private State state = State.LOGGED_OUT;

    private UserInterface ui = new PreloginUI();

    private List<GameData> gameList;

    private List<Integer> gameIDs = new ArrayList<>();

    private Data() {
    }

    public List<Integer> getGameIDs() {
        return gameIDs;
    }

    public void addGameID(int gameID) {
        gameIDs.add(gameID);
    }

    // public void removeGameID(int gameID) {
    // gameIDs.remove(gameIDs.indexOf(gameID));
    // }

    // public void clearGameIDs() {
    // this.gameIDs = List.of();
    // }

    public List<GameData> getGameList() {
        return gameList;
    }

    public void setGameList(List<GameData> gameList) {
        this.gameList = gameList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void initializeRun(String url, int port, UserREPL ui) {
        serverFacade = new ServerFacade("http://%s:%s".formatted(url, port));
    }

    public State getState() {
        return state;
    }

    public UserInterface getUi() {
        return ui;
    }

    public void setState(State state) {
        this.state = state;
        ui = switch (state) {
        case LOGGED_OUT -> new PreloginUI();
        case LOGGED_IN -> new PostloginUI();
        // case IN_GAME -> new BaseUI();
        default -> throw new IllegalArgumentException("Unexpected value: " + state);
        };
    }

    public String getPrompt() {
        return switch (this.state) {
        case LOGGED_OUT -> "[LOGGED OUT] >>> ";
        case LOGGED_IN -> "[%s] >>> ".formatted(username);
        case IN_GAME -> "[%s vs %s] >>> ".formatted(username, username);
        };
    }

    public ServerFacade getServerFacade() {
        return serverFacade;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
