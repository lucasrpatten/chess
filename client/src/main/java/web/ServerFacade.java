package web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import com.google.gson.Gson;

import model.AuthData;
import model.GameData;
import model.UserData;
import ui.Data;

public class ServerFacade {
    private final String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    public AuthData login(UserData userData) {
        AuthData authData = 
    }

    private <T> T get(String endpointUrl, String method, Object request, Class<T> responseType) {
        try {
            Gson gson = new Gson();

            URL url = new URL(this.url + endpointUrl);

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
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode() + " Message: "
                        + connection.getResponseMessage());
            }

            String response = new String(connection.getInputStream().readAllBytes(), "UTF-8");
            if (responseType != null) {
                return gson.fromJson(response, responseType);
            }
            return null;
        }
        catch (IOException e) {
            throw new RuntimeException("Error connecting to server: " + e.getMessage());
        }
    }
}
