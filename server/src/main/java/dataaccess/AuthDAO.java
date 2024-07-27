package dataaccess;

import model.AuthData;

public interface AuthDAO {

    /**
     * @param authData the auth data to add to the database
     * @throws DataAccessException if there is an error
     */
    void addAuth(AuthData authData) throws DataAccessException;

    /**
     * Clear the database of all auth tokens
     * 
     * @throws DataAccessException if there is an error
     */
    void clear() throws DataAccessException;

    /**
     * @param authToken the auth token to delete
     * @throws DataAccessException if there is an error
     */
    void deleteAuth(String authToken) throws DataAccessException;

    /**
     * @param authToken the auth token to get
     * @return the auth data
     * @throws DataAccessException if there is an error
     */
    AuthData getAuth(String authToken) throws DataAccessException;

}