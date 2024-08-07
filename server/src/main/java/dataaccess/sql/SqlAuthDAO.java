package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class SqlAuthDAO extends SqlDAO implements AuthDAO {

    public SqlAuthDAO() throws DataAccessException {
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        if (getAuth(authData.authToken()) != null) {
            throw new DataAccessException("Error: Auth token already exists");
        }
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        update(statement, authData.authToken(), authData.username());
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM auth";
        update(statement);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (getAuth(authToken) == null) {
            throw new DataAccessException("Error: Auth token does not exist, cannot delete");
        }
        String statement = "DELETE FROM auth WHERE authToken = ?";
        update(statement, authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT * FROM auth WHERE authToken = ?";
        return query(statement, returnStatement -> {
            if (returnStatement.next()) {
                return new AuthData(returnStatement.getString("authToken"), returnStatement.getString("username"));
            }
            return null;
        }, authToken);
    }

    @Override
    protected String[] createQuery() {
        String statement = """
                CREATE TABLE IF NOT EXISTS `auth` (
                    `authToken` varchar(64) NOT NULL,
                    `username` varchar(64) NOT NULL
                )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
                """;
        return new String[] { statement };
    }

}
