package service;

import java.security.SecureRandom;
import java.util.Base64;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginRequest;
import model.UserData;

public class UserService {
    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {
        dataAccess.getUserDAO().addUser(user);
        String token = generateAuthToken();
        AuthData authData = new AuthData(token, user.username());
        dataAccess.getAuthDAO().addAuth(authData);
        return authData;
    }

    // this is pretty insecure but idrc cause it's only for school, not production
    public AuthData login(LoginRequest loginRequest) throws DataAccessException {
        if (!dataAccess.getUserDAO().userExists(loginRequest.username())) {
            throw new DataAccessException("User " + loginRequest.username() + " does not exist");
        }
        if (!dataAccess.getUserDAO().validLogin(loginRequest)) {
            throw new DataAccessException("Wrong password");
        }

        String token = generateAuthToken();
        AuthData authData = new AuthData(token, loginRequest.username());
        dataAccess.getAuthDAO().addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws DataAccessException {
        dataAccess.getAuthDAO().deleteAuth(authToken);
    }

    // Function from https://stackoverflow.com/a/56628391/15517956
    private String generateAuthToken() {
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return base64Encoder.encodeToString(bytes);
    }
}
