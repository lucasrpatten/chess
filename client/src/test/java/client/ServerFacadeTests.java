package client;

import org.junit.jupiter.api.*;

import model.AuthData;
import model.UserData;
import server.Server;
import service.ClearService;
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
    @DisplayName("Register User")
    public void registerSuccess() {
        AuthData res = facade.register(new UserData("testRegister", "testRegister", "testReg@mail.com"));
        Assertions.assertNotNull(res);
        Assertions.assertNotNull(res.authToken());
        Assertions.assertEquals("testRegister", res.username());
    }

    @Test
    @DisplayName("Register Fail")
    public void registerFailure() {
        UserData req = new UserData("testRegister", "testRegister", "testReg@mail.com");
        Assertions.assertThrows(Exception.class, () -> facade.register(req));
    }

}
