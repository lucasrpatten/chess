package dataaccess.sql;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class SqlDataAccess implements DataAccess {

    private final SqlAuthDAO authDAO;

    private final SqlGameDAO gameDAO;

    private final SqlUserDAO userDAO;

    public SqlDataAccess() {
        try {
            authDAO = new SqlAuthDAO();
            gameDAO = new SqlGameDAO();
            userDAO = new SqlUserDAO();
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SqlAuthDAO getAuthDAO() {
        return authDAO;
    }

    @Override
    public SqlGameDAO getGameDAO() {
        return gameDAO;
    }

    @Override
    public SqlUserDAO getUserDAO() {
        return userDAO;
    }

}