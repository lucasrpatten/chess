package dataaccess;

import dataaccess.mem.MemAuthDAO;
import dataaccess.mem.MemUserDAO;

public interface DataAccess {

    MemAuthDAO getAuthDAO();

    UserDAO
    GameDAO getGameDAO();

}