package dataaccess;

import java.util.HashMap;
import model.AuthData;

public class AuthDAO {
    private final HashMap<String, AuthData> tokens = new HashMap<>();

    public void addAuth(AuthData authData) throws DataAccessException {
        if (tokens.containsKey(authData.authToken())) {
            throw new DataAccessException("Error: Token already exists");
        }
        tokens.put(authData.authToken(), authData);
    }

    public void clear() throws DataAccessException {
        tokens.clear();
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        tokens.remove(authToken);

    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return tokens.get(authToken);
    }

}
