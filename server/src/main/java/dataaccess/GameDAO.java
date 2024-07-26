package dataaccess;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import model.GameData;

public class GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();

    /**
     * @param gameData Game to add to the database
     * @return the added game
     * @throws DataAccessException if there is an error
     */
    public GameData addGame(GameData gameData) throws DataAccessException {
        if (gameData.game() == null)
            throw new DataAccessException("Cannot add a null game to the database");
        int id = games.size() + 1;
        GameData game = new GameData(id, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(),
                gameData.game());
        games.put(id, game);
        return game;
    }

    /**
     * Clear all games from the database
     * 
     * @throws DataAccessException if there is an error
     */
    public void clear() throws DataAccessException {
        games.clear();
    }

    /**
     * @param gameID the ID of the game
     * @return the game with the given ID
     * @throws DataAccessException if there is an error
     */
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    /**
     * @param gameData the game to update
     * @throws DataAccessException if there is an error
     */
    public void updateGame(GameData gameData) throws DataAccessException {
        if (!games.containsKey(gameData.gameID()))
            throw new DataAccessException("Game does not exist (so it can't be updated)");
        if (gameData.game() == null)
            throw new DataAccessException("Cannot update a null game");
        games.remove(gameData.gameID());
        games.put(gameData.gameID(), gameData);
    }

    /**
     * @return a list of all games
     * @throws DataAccessException if there is an error
     */
    public Collection<GameData> listGames() throws DataAccessException {
        return Collections.unmodifiableCollection(games.values());
    }

}
