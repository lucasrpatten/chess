package dataaccess;

import java.util.HashMap;
import model.AuthData;

public class AuthDAO {
    private final HashMap<String, AuthData> tokens = new HashMap<>();

    /**
     * @param authData the auth data to add to the database
     * @throws DataAccessException if there is an error
     */
    public void addAuth(AuthData authData) throws DataAccessException {
        if (tokens.containsKey(authData.authToken())) {
            throw new DataAccessException("Error: Token already exists");
        }
        tokens.put(authData.authToken(), authData);
    }

    /**
     * Clear the database of all auth tokens
     * 
     * @throws DataAccessException if there is an error
     */
    public void clear() throws DataAccessException {
        tokens.clear();
    }

    /**
     * @param authToken the auth token to delete
     * @throws DataAccessException if there is an error
     */
    public void deleteAuth(String authToken) throws DataAccessException {
        tokens.remove(authToken);

    }

    /**
     * @param authToken the auth token to get
     * @return the auth data
     * @throws DataAccessException if there is an error
     */
    public AuthData getAuth(String authToken) throws DataAccessException {
        return tokens.get(authToken);
    }

}
