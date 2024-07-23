package dataaccess;

import java.util.Collection;

import model.GameData;

public interface GameDAO {
    void clear() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    GameData addGame(GameData game) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;
}
