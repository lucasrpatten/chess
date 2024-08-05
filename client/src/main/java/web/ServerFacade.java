package web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.gson.Gson;

import model.AuthData;
import model.EmptyRequest;
import model.LoginRequest;
import model.UserData;
import ui.Data;

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

    public void logout(AuthData authData) {
        request("/session", "DELETE", authData, AuthData.class);
        Data.getInstance().setAuthToken(null);
        Data.getInstance().setUsername(null);
        Data.getInstance().setState(Data.State.LOGGED_OUT);
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
