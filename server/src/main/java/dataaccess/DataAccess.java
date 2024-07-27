package dataaccess;

import dataaccess.mem.MemAuthDAO;
import dataaccess.mem.MemUserDAO;

public interface DataAccess {

    AuthDAO getAuthDAO();

    UserDAO getUserDAO();

    GameDAO getGameDAO();

}