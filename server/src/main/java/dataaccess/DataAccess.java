package dataaccess;

public interface DataAccess {

    AuthDAO getAuthDAO();

    UserDAO getUserDAO();

    GameDAO getGameDAO();

}