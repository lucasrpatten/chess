package dataaccess.mem;

import java.util.HashMap;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.LoginRequest;
import model.UserData;

public class MemUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    /**
     * Add a new user to the database
     * 
     * @param user the user to add
     * @throws DataAccessException if there is an error
     */
    @Override
    public void addUser(UserData user) throws DataAccessException {
        if (userExists(user.username())) {
            throw new DataAccessException("The user already exists");
        }
        users.put(user.username(), user);
    }

    /**
     * Clear all users from the database
     * 
     * @throws DataAccessException if there is an error
     */
    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    /**
     * Check if a user exists
     * 
     * @param username the username
     * @return true if the user exists
     * @throws DataAccessException if there is an error
     */
    @Override
    public boolean userExists(String username) throws DataAccessException {
        return users.containsKey(username);
    }

    /**
     * Check if a login is valid
     * 
     * @param login the login request
     * @return true if the login is valid
     * @throws DataAccessException if there is an error
     */
    @Override
    public boolean validLogin(LoginRequest login) throws DataAccessException {
        UserData dbUser = users.get(login.username());
        return dbUser != null && dbUser.password().equals(login.password());
    }

}
