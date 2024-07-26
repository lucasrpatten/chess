package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dataaccess.DataAccess;
import model.AuthData;
import model.LoginRequest;
import model.UserData;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {

    private static UserData existingUser;
    private static UserData newUser;

    private static DataAccess dataAccess;
    private UserService userService;
    private ClearService clearService;

    @BeforeAll
    public static void init() {
        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new UserData("NewUser", "newUserPassword", "nu@mail.com");

        dataAccess = new DataAccess();
    }

    @BeforeEach
    public void setup() throws ServerException {
        userService = new UserService(dataAccess);
        new GameService(dataAccess);
        clearService = new ClearService(dataAccess);
        // Clear database before each test
        clearService.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Bad Register")
    public void badRegister() {
        // attempt to register an empty user
        UserData registerRequest = new UserData(null, null, null);
        assertThrows(BadRequestException.class, () -> {
            userService.register(registerRequest);
        });
    }

    @Test
    @Order(2)
    @DisplayName("Successful Register")
    public void successfulRegister() throws ServerException {
        AuthData authData = userService.register(newUser);
        assertNotNull(authData);
        assertEquals(newUser.username(), authData.username());
    }

    @Test
    @Order(3)
    @DisplayName("Duplicate User Registration")
    public void duplicateUserRegistration() throws ServerException {
        // Register the user first
        userService.register(existingUser);

        // Attempt to register the same user again
        assertThrows(AlreadyTakenException.class, () -> {
            userService.register(existingUser);
        });
    }

    @Test
    @Order(4)
    @DisplayName("Successful Login")
    public void successfulLogin() throws ServerException {
        // Register the user first
        userService.register(newUser);

        // Attempt to login with the registered user
        LoginRequest loginRequest = new LoginRequest(newUser.username(), newUser.password());
        AuthData authData = userService.login(loginRequest);

        assertNotNull(authData);
        assertEquals(newUser.username(), authData.username());
        authData.authToken();
    }

    @Test
    @Order(5)
    @DisplayName("Login with Wrong Credentials")
    public void loginWithWrongCredentials() throws ServerException {
        // Register the user first
        userService.register(newUser);

        // Attempt to login with wrong password
        LoginRequest loginRequest = new LoginRequest(newUser.username(), "wrongPassword");
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(loginRequest);
        });
    }

    @Test
    @Order(6)
    @DisplayName("Successful Logout")
    public void successfulLogout() throws ServerException {
        // Register and login the user first
        userService.register(newUser);
        LoginRequest loginRequest = new LoginRequest(newUser.username(), newUser.password());
        AuthData authData = userService.login(loginRequest);

        // Attempt to logout
        userService.logout(authData.authToken());

        // Verify logout by attempting to logout again with the same token
        assertThrows(UnauthorizedException.class, () -> {
            userService.logout(authData.authToken());
        });
    }

    @Test
    @Order(7)
    @DisplayName("Logout with Invalid Token")
    public void logoutWithInvalidToken() {
        // Attempt to logout with an invalid token
        assertThrows(UnauthorizedException.class, () -> {
            userService.logout("invalidToken");
        });
    }
}
