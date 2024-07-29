package dataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import dataaccess.sql.SqlDataAccess;
import dataaccess.sql.SqlGameDAO;
import model.GameData;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqlGameDAOTests {
    private static SqlDataAccess dataAccess;
    private static SqlGameDAO sqlGameDAO;
    private static GameData testGame1;

    @BeforeAll
    public static void setUp() throws Exception {
        dataAccess = new SqlDataAccess();
        sqlGameDAO = dataAccess.getGameDAO();

        testGame1 = new GameData(1, "user1", "user2", "game1", new ChessGame());
    }

    @BeforeEach
    public void clear() throws Exception {
        sqlGameDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Add Game Success")
    public void addGameSuccess() throws Exception {
        sqlGameDAO.addGame(testGame1);
        assertNotNull(sqlGameDAO.getGame(1));
    }

    @Test
    @Order(2)
    @DisplayName("Get Game Success")
    public void getGameSuccess() throws Exception {
        sqlGameDAO.addGame(testGame1);
        assertNotNull(sqlGameDAO.getGame(1));
    }

    @Test
    @Order(3)
    @DisplayName("List Games Success")
    public void listGamesSuccess() throws Exception {
        GameData testGame2 = new GameData(2, "user1", "user2", "game2", new ChessGame());
        sqlGameDAO.addGame(testGame1);
        sqlGameDAO.addGame(testGame2);
        Collection<GameData> games = sqlGameDAO.listGames();
        for (GameData game : games) {
            if (game.gameID() == 1) {
                assertEquals(testGame1, game);
            }
            else if (game.gameID() == 2) {
                assertEquals(testGame2, game);
            }
            else {
                throw new IllegalStateException();
            }
        }
    }

    @Test
    @Order(4)
    @DisplayName("Update Game Success")
    public void updateGameSuccess() throws Exception {
        sqlGameDAO.addGame(testGame1);
        ChessGame testGame1ChessGame = new ChessGame();
        testGame1ChessGame.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        sqlGameDAO.updateGame(new GameData(testGame1.gameID(), testGame1.whiteUsername(), testGame1.blackUsername(),
                testGame1.gameName(), testGame1ChessGame));
        assertEquals(testGame1ChessGame, sqlGameDAO.getGame(1).game());
    }

    @Test
    @Order(5)
    @DisplayName("Clear Games")
    public void clearGames() throws Exception {
        sqlGameDAO.addGame(testGame1);
        sqlGameDAO.clear();
        assertNull(sqlGameDAO.getGame(1));
    }
}
