package dataaccess;

import java.util.HashMap;

import model.LoginRequest;
import model.UserData;

public class UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    public void addUser(UserData user) throws DataAccessException {
        if (userExists(user.username())) {
            throw new DataAccessException("The user already exists");
        }
        users.put(user.username(), user);
    }

    public void clear() throws DataAccessException {
        users.clear();
    }

    public boolean userExists(String username) throws DataAccessException {
        return users.containsKey(username);
    }

    public boolean validLogin(LoginRequest login) throws DataAccessException {
        UserData dbUser = users.get(login.username());
        return dbUser != null && dbUser.password().equals(login.password());
    }

}
