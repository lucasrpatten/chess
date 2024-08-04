package ui;

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

    private String authToken;

    private Data() {
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
