package dataaccess;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dataaccess.sql.SqlAuthDAO;
import dataaccess.sql.SqlDataAccess;
import model.AuthData;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqlAuthDAOTests {
    private static SqlDataAccess dataAccess;
    private static SqlAuthDAO sqlAuthDAO;
    private static AuthData testAuth1;

    @BeforeAll
    public static void setUp() throws Exception {
        dataAccess = new SqlDataAccess();
        sqlAuthDAO = dataAccess.getAuthDAO();

        testAuth1 = new AuthData("auth1", "user1");
    }

    @BeforeEach
    public void clear() throws Exception {
        sqlAuthDAO.clear();
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

    @Test
    @Order(4)
    @DisplayName("Delete Auth")
    public void deleteAuthTest() throws DataAccessException {
        sqlAuthDAO.addAuth(testAuth1);
        sqlAuthDAO.deleteAuth(testAuth1.authToken());
        assertNull(sqlAuthDAO.getAuth(testAuth1.authToken()));
    }

    @Test
    @Order(5)
    @DisplayName("Delete Non-Existent Auth")
    public void deleteNonExistentAuthTest() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> {
            sqlAuthDAO.deleteAuth(testAuth1.authToken());
        });
    }

    @Test
    @Order(6)
    @DisplayName("Get Auth Without Auth Token")
    public void getAuthWithoutAuthToken() throws DataAccessException {
        assertNull(sqlAuthDAO.getAuth(null));
    }

    @Test
    @Order(7)
    @DisplayName("Add Multiple Auths")
    public void addMultipleAuths() throws DataAccessException {
        AuthData testAuth2 = new AuthData("auth2", "user2");
        sqlAuthDAO.addAuth(testAuth1);
        sqlAuthDAO.addAuth(testAuth2);
        assertNotNull(sqlAuthDAO.getAuth(testAuth1.authToken()));
        assertNotNull(sqlAuthDAO.getAuth(testAuth2.authToken()));
    }

    @Test
    @Order(8)
    @DisplayName("Clear Auth")
    public void clearAuth() throws DataAccessException {
        sqlAuthDAO.addAuth(testAuth1);
        sqlAuthDAO.clear();
        assertNull(sqlAuthDAO.getAuth(testAuth1.authToken()));
    }
}
