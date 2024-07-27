package dataaccess;

import model.LoginRequest;
import model.UserData;

public interface UserDAO {

    /**
     * Add a new user to the database
     * 
     * @param user the user to add
     * @throws DataAccessException if there is an error
     */
    void addUser(UserData user) throws DataAccessException;

    /**
     * Clear all users from the database
     * 
     * @throws DataAccessException if there is an error
     */
    void clear() throws DataAccessException;

    /**
     * Check if a user exists
     * 
     * @param username the username
     * @return true if the user exists
     * @throws DataAccessException if there is an error
     */
    boolean userExists(String username) throws DataAccessException;

    /**
     * Check if a login is valid
     * 
     * @param login the login request
     * @return true if the login is valid
     * @throws DataAccessException if there is an error
     */
    boolean validLogin(LoginRequest login) throws DataAccessException;

}