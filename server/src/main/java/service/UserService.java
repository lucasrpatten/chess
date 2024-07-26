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

    public AuthData register(UserData user) throws ServerException {
        try {
            if (user == null) {
                throw new BadRequestException("Error: User cannot be null");
            }
            else if (user.username() == null) {
                throw new BadRequestException("Error: Username cannot be null");
            }
            else if (user.password() == null) {
                throw new BadRequestException("Error: Password cannot be null");
            }
            else if (user.email() == null) {
                throw new BadRequestException("Error: Email cannot be null");
            }

            if (dataAccess.getUserDAO().userExists(user.username())) {
                throw new AlreadyTakenException("Error: Username %s is already taken".formatted(user.username()));
            }
            dataAccess.getUserDAO().addUser(user);
            String token = generateAuthToken();
            AuthData authData = new AuthData(token, user.username());
            dataAccess.getAuthDAO().addAuth(authData);
            return authData;
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }

    // this is pretty insecure but idrc cause it's only for school, not production
    public AuthData login(LoginRequest loginRequest) throws ServerException {
        try {
            if (!dataAccess.getUserDAO().userExists(loginRequest.username())) {
                throw new UnauthorizedException("User %s does not exist".formatted(loginRequest.username()));
            }
            if (!dataAccess.getUserDAO().validLogin(loginRequest)) {
                throw new UnauthorizedException("Error: Wrong password");
            }

            String token = generateAuthToken();
            AuthData authData = new AuthData(token, loginRequest.username());
            dataAccess.getAuthDAO().addAuth(authData);
            return authData;
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }

    public void logout(String authToken) throws ServerException {
        try {
            AuthData authData = dataAccess.getAuthDAO().getAuth(authToken);
            if (authData == null) {
                throw new UnauthorizedException("Error: Invalid token. Unauthorized request");
            }
            dataAccess.getAuthDAO().deleteAuth(authToken);
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }

    // Function from https://stackoverflow.com/a/56628391/15517956
    private String generateAuthToken() {
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return base64Encoder.encodeToString(bytes);
    }
}
