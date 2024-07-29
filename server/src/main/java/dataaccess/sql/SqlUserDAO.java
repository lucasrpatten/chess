package dataaccess.sql;

import org.mindrot.jbcrypt.BCrypt;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.LoginRequest;
import model.UserData;

public class SqlUserDAO extends SqlDAO implements UserDAO {

    public SqlUserDAO() throws DataAccessException {
        super();
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        update(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE users;";
        update(statement);
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        String statement = "SELECT username FROM users WHERE username = ?;";
        return query(statement, returnStatement -> returnStatement.next(), username);
    }

    @Override
    public boolean validLogin(LoginRequest login) throws DataAccessException {
        String statement = "SELECT username, password FROM users WHERE username = ?;";
        return query(statement, returnStatement -> {
            if (returnStatement.next()) {
                String hashedPassword = returnStatement.getString("password");
                return BCrypt.checkpw(login.password(), hashedPassword);
            }
            return false;
        }, login.username());
    }

    @Override
    protected String[] createQuery() {
        String statement = """
                CREATE TABLE IF NOT EXISTS `users` (
                    `username` varchar(64) NOT NULL PRIMARY KEY,
                    `password` varchar(64) NOT NULL,
                    `email` varchar(64) NOT NULL
                )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                """;

        return new String[] { statement };
    }

}
