package dataaccess;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import dataaccess.sql.SqlDataAccess;
import dataaccess.sql.SqlUserDAO;
import model.LoginRequest;
import model.UserData;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqlUserDAOTests {
    private static SqlDataAccess dataAccess;
    static SqlUserDAO sqlUserDAO;
    private static UserData testUser1;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        dataAccess = new SqlDataAccess();
        sqlUserDAO = dataAccess.getUserDAO();

        testUser1 = new UserData("user1", "pass1", "user1@email.com");
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        sqlUserDAO.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Add User Success")
    public void addUserSuccess() throws DataAccessException {
        sqlUserDAO.addUser(testUser1);
        assertTrue(sqlUserDAO.userExists(testUser1.username()));
    }

    @Test
    @Order(2)
    @DisplayName("Add User Already Exists")
    public void addUserAlreadyExists() throws DataAccessException {
        sqlUserDAO.addUser(testUser1);
        assertThrows(DataAccessException.class, () -> {
            sqlUserDAO.addUser(testUser1);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Add Multiple Users")
    public void addMultipleUsers() throws DataAccessException {
        UserData testUser2 = new UserData("user2", "pass2", "user2@email.com");
        UserData testUser3 = new UserData("user3", "pass3", "user3@email.com");
        sqlUserDAO.addUser(testUser1);
        sqlUserDAO.addUser(testUser2);
        sqlUserDAO.addUser(testUser3);
        assertTrue(sqlUserDAO.userExists(testUser1.username()));
        assertTrue(sqlUserDAO.userExists(testUser2.username()));
        assertTrue(sqlUserDAO.userExists(testUser3.username()));
    }

    @Test
    @Order(4)
    @DisplayName("Login Success")
    public void loginSuccess() throws DataAccessException {
        sqlUserDAO.addUser(testUser1);
        assertTrue(sqlUserDAO.validLogin(new LoginRequest(testUser1.username(), testUser1.password())));
    }

    @Test
    @Order(5)
    @DisplayName("Login Failed")
    public void loginFailed() throws DataAccessException {
        sqlUserDAO.addUser(testUser1);
        assertFalse(sqlUserDAO.validLogin(new LoginRequest(testUser1.username(), "wrongPassword")));
    }

    @Test
    @Order(6)
    @DisplayName("User Does Not Exist")
    public void userDoesNotExist() throws DataAccessException {
        assertFalse(sqlUserDAO.userExists("doesNotExist"));
    }

    @Test
    @Order(7)
    @DisplayName("Clear Users")
    public void clearUsers() throws DataAccessException {
        sqlUserDAO.addUser(testUser1);
        sqlUserDAO.clear();
        assertFalse(sqlUserDAO.userExists(testUser1.username()));
    }
}