package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class SqlDataAccess implements DataAccess {

    private final AuthDAO authDAO;

    private final GameDAO gameDAO;

    private final UserDAO userDAO;

    public SqlDataAccess() throws DataAccessException {
        authDAO = new SqlAuthDAO();
        gameDAO = new SqlGameDAO();
        userDAO = new SqlUserDAO();
    }

    @Override
    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    @Override
    public GameDAO getGameDAO() {
        return gameDAO;
    }

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

}