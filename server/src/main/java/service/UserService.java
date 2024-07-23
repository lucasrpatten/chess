package service;

import java.security.SecureRandom;
import java.util.Base64;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.LoginRequest;
import model.UserData;

public class UserService {
    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private static final AuthDAO authDAO = new AuthDAO();
    private static final UserDAO userDAO = new UserDAO();

    public AuthData register(UserData user) throws DataAccessException {
        userDAO.addUser(user);
        String token = generateAuthToken();
        AuthData authData = new AuthData(token, user.username());
        authDAO.addAuth(authData);
        return authData;
    }

    // this is pretty insecure but idrc cause it's only for school, not production
    public AuthData login(LoginRequest loginRequest) throws DataAccessException {
        if (!userDAO.userExists(loginRequest.username())) {
            throw new DataAccessException("User " + loginRequest.username() + " does not exist");
        }
        if (!userDAO.validLogin(loginRequest)) {
            throw new DataAccessException("Wrong password");
        }

        String token = generateAuthToken();
        AuthData authData = new AuthData(token, loginRequest.username());
        authDAO.addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

    // Function from https://stackoverflow.com/a/56628391/15517956
    private String generateAuthToken() {
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return base64Encoder.encodeToString(bytes);
    }
}
