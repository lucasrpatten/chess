package dataaccess.mem;

import dataaccess.DataAccess;

public class MemDataAccess implements DataAccess {
    private final MemAuthDAO authDAO;
    private final MemUserDAO userDAO;
    private final MemGameDAO gameDAO;

    public MemDataAccess() {
        authDAO = new MemAuthDAO();
        userDAO = new MemUserDAO();
        gameDAO = new MemGameDAO();
    }

    @Override
    public MemAuthDAO getAuthDAO() {
        return authDAO;
    }

    @Override
    public MemUserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public MemGameDAO getGameDAO() {
        return gameDAO;
    }
}
