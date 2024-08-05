package client;

import java.util.Collection;

import org.junit.jupiter.api.*;

import model.AuthData;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;
import model.GameListResult;
import model.LoginRequest;
import model.UserData;
import server.Server;
import service.ClearService;
import ui.Data;
import web.ServerFacade;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @DisplayName("Register Success")
    @Order(1)
    public void registerSuccess() {
        AuthData res = facade.register(new UserData("testUser", "password", "testUser@mail.com"));
        Assertions.assertNotNull(res);
        Assertions.assertNotNull(res.authToken());
        Assertions.assertEquals("testUser", res.username());
    }

    @Test
    @DisplayName("Register Fail")
    @Order(2)
    public void registerFailure() {
        UserData req = new UserData("testUser", "password", "testUser@mail.com");
        Assertions.assertThrows(Exception.class, () -> facade.register(req));
    }

    @Test
    @DisplayName("Logout Fail")
    @Order(3)
    public void logoutFailure() {
        String oldAuth = Data.getInstance().getAuthToken();
        Data.getInstance().setAuthToken("InvalidToken");
        Assertions.assertThrows(Exception.class, () -> facade.logout());
        Data.getInstance().setAuthToken(oldAuth);
    }

    @Test
    @DisplayName("Logout Success")
    @Order(4)
    public void logoutSuccess() {
        facade.logout();
        Assertions.assertEquals(Data.State.LOGGED_OUT, Data.getInstance().getState());
        Assertions.assertNull(Data.getInstance().getAuthToken());
        Assertions.assertNull(Data.getInstance().getUsername());
    }

    @Test
    @DisplayName("Login Fail")
    @Order(5)
    public void loginFailure() {

        Data.getInstance().setState(Data.State.LOGGED_OUT);
        LoginRequest req = new LoginRequest("NonExistent", "NonExistent");
        Assertions.assertThrows(Exception.class, () -> facade.login(req));
    }

    @Test
    @DisplayName("Login Success")
    @Order(6)
    public void loginSuccess() {
        AuthData res = facade.login(new LoginRequest("testUser", "password"));
        Assertions.assertNotNull(res);
        Assertions.assertNotNull(res.authToken());
        Assertions.assertEquals("testUser", res.username());
    }

    @Test
    @DisplayName("Create Game Success")
    @Order(7)
    public void createGameSuccess() {
        CreateGameResult res = facade.createGame(new CreateGameRequest("testGame"));
        Assertions.assertNotNull(res);
        Assertions.assertNotNull(res.gameID());
        Assertions.assertTrue(res.gameID() > 0);
    }

    @Test
    @DisplayName("Create Game Fail")
    @Order(8)
    public void createGameFailure() {
        String oldAuth = Data.getInstance().getAuthToken();
        Data.getInstance().setAuthToken("InvalidToken");
        Assertions.assertThrows(Exception.class, () -> facade.createGame(new CreateGameRequest("GameName")));
        Data.getInstance().setAuthToken(oldAuth);
    }

    @Test
    @DisplayName("List Games Fail")
    @Order(9)
    public void listGamesFailure() {
        String oldAuth = Data.getInstance().getAuthToken();
        Data.getInstance().setAuthToken("InvalidToken");
        Assertions.assertThrows(Exception.class, () -> facade.listGames());
        Data.getInstance().setAuthToken(oldAuth);
    }

    @Test
    @DisplayName("List Games Success")
    @Order(10)
    public void listGamesSuccess() {
        GameListResult res = facade.listGames();
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.games().isEmpty());
    }

}
