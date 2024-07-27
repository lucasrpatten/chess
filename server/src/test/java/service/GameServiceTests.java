package service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import chess.ChessGame.TeamColor;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.mem.MemDataAccess;
import model.AuthData;
import model.CreateGameResult;
import model.GameData;
import model.GameListResult;
import model.UserData;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTests {

    private static DataAccess dataAccess;
    private UserService userService;
    private GameService gameService;
    private ClearService clearService;
    private static UserData testUser;
    private static String authToken;

    @BeforeAll
    public static void init() throws ServerException {
        dataAccess = new MemDataAccess();
        testUser = new UserData("TestUser", "testPassword", "test@mail.com");
    }

    @BeforeEach
    public void setup() throws ServerException {
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        clearService = new ClearService(dataAccess);
        clearService.clear();

        AuthData authData = userService.register(testUser);
        authToken = authData.authToken();
    }

    @Test
    @Order(1)
    @DisplayName("List Games with Valid Auth Token")
    public void listGamesWithValidAuthToken() throws ServerException {
        GameListResult gameListResult = gameService.list(authToken);
        assertNotNull(gameListResult);
        assertEquals(0, gameListResult.games().size());
    }

    @Test
    @Order(2)
    @DisplayName("List Games with Invalid Auth Token")
    public void listGamesWithInvalidAuthToken() {
        assertThrows(UnauthorizedException.class, () -> {
            gameService.list("invalidToken");
        });
    }

    @Test
    @Order(3)
    @DisplayName("Create Game with Valid Auth Token")
    public void createGameWithValidAuthToken() throws ServerException {
        CreateGameResult result = gameService.create("Test Game", authToken);
        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    @Order(4)
    @DisplayName("Create Game with Invalid Auth Token")
    public void createGameWithInvalidAuthToken() {
        assertThrows(UnauthorizedException.class, () -> {
            gameService.create("Test Game", "invalidToken");
        });
    }

    @Test
    @Order(5)
    @DisplayName("Create Game with Null Game Name")
    public void createGameWithNullGameName() {
        assertThrows(BadRequestException.class, () -> {
            gameService.create(null, authToken);
        });
    }

    @Test
    @Order(6)
    @DisplayName("Join Game with Valid Auth Token and Color")
    public void joinGameWithValidAuthTokenAndColor() throws ServerException, DataAccessException {
        CreateGameResult createResult = gameService.create("Test Game", authToken);
        gameService.join(TeamColor.WHITE, createResult.gameID(), authToken);

        GameData game = dataAccess.getGameDAO().getGame(createResult.gameID());
        assertNotNull(game);
        assertEquals("TestUser", game.whiteUsername());
    }

    @Test
    @Order(7)
    @DisplayName("Join Game with Invalid Auth Token")
    public void joinGameWithInvalidAuthToken() throws ServerException {
        CreateGameResult createResult = gameService.create("Test Game", authToken);

        assertThrows(UnauthorizedException.class, () -> {
            gameService.join(TeamColor.WHITE, createResult.gameID(), "invalidToken");
        });
    }

    @Test
    @Order(8)
    @DisplayName("Join Game with Null Color")
    public void joinGameWithNullColor() throws ServerException {
        CreateGameResult createResult = gameService.create("Test Game", authToken);

        assertThrows(BadRequestException.class, () -> {
            gameService.join(null, createResult.gameID(), authToken);
        });
    }

    @Test
    @Order(9)
    @DisplayName("Join Game with Already Taken Color")
    public void joinGameWithAlreadyTakenColor() throws ServerException {
        CreateGameResult createResult = gameService.create("Test Game", authToken);
        gameService.join(TeamColor.WHITE, createResult.gameID(), authToken);

        AuthData anotherAuthData = userService
                .register(new UserData("AnotherUser", "anotherPassword", "another@mail.com"));
        assertThrows(AlreadyTakenException.class, () -> {
            gameService.join(TeamColor.WHITE, createResult.gameID(), anotherAuthData.authToken());
        });
    }

    @Test
    @Order(10)
    @DisplayName("Join Non-existent Game")
    public void joinNonExistentGame() {
        assertThrows(BadRequestException.class, () -> {
            gameService.join(TeamColor.WHITE, 9999, authToken);
        });
    }

    @Test
    @Order(11)
    @DisplayName("Create Game with Duplicate Game Name")
    public void createGameWithDuplicateName() throws ServerException {
        // Create a game with a given name
        gameService.create("Duplicate Game", authToken);

        // Attempt to create another game with the same name (which is allowed)
        assertDoesNotThrow(() -> {
            gameService.create("Duplicate Game", authToken);
        });
    }

    @Test
    @Order(12)
    @DisplayName("Join Game with Invalid Game ID")
    public void joinGameWithInvalidGameID() throws ServerException {
        // Try joining a game with a non-existent game ID
        assertThrows(BadRequestException.class, () -> {
            gameService.join(TeamColor.WHITE, -1, authToken);
        });
    }

    @Test
    @Order(13)
    @DisplayName("Create Game with Empty Game Name")
    public void createGameWithEmptyGameName() {
        // Attempt to create a game with an empty name
        assertThrows(BadRequestException.class, () -> {
            gameService.create(null, authToken);
        });
    }

    @Test
    @Order(14)
    @DisplayName("Join Game with Invalid Team Color")
    public void joinGameWithInvalidTeamColor() throws ServerException {
        CreateGameResult createResult = gameService.create("Test Game", authToken);

        // Attempt to join with an invalid team color (assuming invalid color is not
        // handled)
        assertThrows(BadRequestException.class, () -> {
            gameService.join(null, createResult.gameID(), authToken);
        });
    }

    @Test
    @Order(15)
    @DisplayName("Create Game Without Auth Token")
    public void createGameWithoutAuthToken() {
        // Attempt to create a game without an auth token
        assertThrows(UnauthorizedException.class, () -> {
            gameService.create("Game Without Auth Token", null);
        });
    }

    @Test
    @Order(16)
    @DisplayName("Join Game Without Auth Token")
    public void joinGameWithoutAuthToken() throws ServerException {
        CreateGameResult createResult = gameService.create("Test Game", authToken);

        // Attempt to join a game without an auth token
        assertThrows(UnauthorizedException.class, () -> {
            gameService.join(TeamColor.WHITE, createResult.gameID(), null);
        });
    }

    @Test
    @Order(17)
    @DisplayName("Create Game with Invalid Auth Token Length")
    public void createGameWithInvalidAuthTokenLength() {
        // Attempt to create a game with an invalid auth token length (e.g., too short)
        assertThrows(UnauthorizedException.class, () -> {
            gameService.create("Game with Invalid Token", "shortToken");
        });
    }

}
