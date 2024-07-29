package dataaccess;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dataaccess.sql.SqlAuthDAO;
import dataaccess.sql.SqlDataAccess;
import dataaccess.sql.SqlGameDAO;
import dataaccess.sql.SqlUserDAO;
import model.AuthData;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqlAuthDAOTests {
    private static SqlDataAccess dataAccess;
    private static SqlAuthDAO sqlAuthDAO;
    private static SqlUserDAO sqlUserDAO;
    private static SqlGameDAO sqlGameDAO;
    private static AuthData testAuth1;

    @BeforeAll
    public static void setUp() throws Exception {
        dataAccess = new SqlDataAccess();
        sqlAuthDAO = dataAccess.getAuthDAO();
        sqlUserDAO = dataAccess.getUserDAO();
        sqlGameDAO = dataAccess.getGameDAO();

        testAuth1 = new AuthData("auth1", "user1");
        sqlAuthDAO.clear();
        sqlUserDAO.clear();
        sqlGameDAO.clear();
    }

    @BeforeEach
    public void clear() throws Exception {
        sqlAuthDAO.clear();
        sqlUserDAO.clear();
        sqlGameDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Add Auth Success")
    public void addAuthSuccess() throws DataAccessException {
        sqlAuthDAO.addAuth(testAuth1);
        assertNotNull(sqlAuthDAO.getAuth(testAuth1.authToken()));
    }

    @Test
    @Order(2)
    @DisplayName("Empty Auth")
    public void getAuthTest() throws DataAccessException {
        assertNull(sqlAuthDAO.getAuth(testAuth1.authToken()));
    }

    @Test
    @Order(3)
    @DisplayName("Auth Already Exists")
    public void addAuthAlreadyExists() throws DataAccessException {
        sqlAuthDAO.addAuth(testAuth1);
        assertThrows(DataAccessException.class, () -> {
            sqlAuthDAO.addAuth(testAuth1);
        });
    }
}
