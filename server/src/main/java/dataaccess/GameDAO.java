package dataaccess;

import java.util.Collection;

import model.GameData;

public interface GameDAO {

    /**
     * @param gameData Game to add to the database
     * @return the added game
     * @throws DataAccessException if there is an error
     */
    GameData addGame(GameData gameData) throws DataAccessException;

    /**
     * Clear all games from the database
     * 
     * @throws DataAccessException if there is an error
     */
    void clear() throws DataAccessException;

    /**
     * @param gameID the ID of the game
     * @return the game with the given ID
     * @throws DataAccessException if there is an error
     */
    GameData getGame(int gameID) throws DataAccessException;

    /**
     * @param gameData the game to update
     * @throws DataAccessException if there is an error
     */
    void updateGame(GameData gameData) throws DataAccessException;

    /**
     * @return a list of all games
     * @throws DataAccessException if there is an error
     */
    Collection<GameData> listGames() throws DataAccessException;

}