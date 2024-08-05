package client;

import org.junit.jupiter.api.*;

import model.AuthData;
import model.LoginRequest;
import model.UserData;
import server.Server;
import service.ClearService;
import ui.Data;
import web.ServerFacade;

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
    public void registerSuccess() {
        AuthData res = facade.register(new UserData("testUser", "password", "testUser@mail.com"));
        Assertions.assertNotNull(res);
        Assertions.assertNotNull(res.authToken());
        Assertions.assertEquals("testUser", res.username());
    }

    @Test
    @DisplayName("Register Fail")
    public void registerFailure() {
        UserData req = new UserData("testUser", "password", "testUser@mail.com");
        Assertions.assertThrows(Exception.class, () -> facade.register(req));
    }

    @Test
    @DisplayName("Logout Fail")
    public void logoutFailure() {
        Assertions.assertThrows(Exception.class, () -> facade.logout(new AuthData("InvalidToken", "testUser")));
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() {
        facade.logout(new AuthData(Data.getInstance().getAuthToken(), Data.getInstance().getUsername()));
        Assertions.assertEquals(Data.State.LOGGED_OUT, Data.getInstance().getState());
        Assertions.assertNull(Data.getInstance().getAuthToken());
        Assertions.assertNull(Data.getInstance().getUsername());
    }

    @Test
    @DisplayName("Login Fail")
    public void loginFailure() {

        Data.getInstance().setState(Data.State.LOGGED_OUT);
        LoginRequest req = new LoginRequest("NonExistent", "NonExistent");
        Assertions.assertThrows(Exception.class, () -> facade.login(req));
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() {
        AuthData res = facade.login(new LoginRequest("testUser", "password"));
        Assertions.assertNotNull(res);
        Assertions.assertNotNull(res.authToken());
        Assertions.assertEquals("testUser", res.username());
    }

}
