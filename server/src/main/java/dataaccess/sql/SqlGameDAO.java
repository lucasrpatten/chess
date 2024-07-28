package dataaccess.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

public class SqlGameDAO extends SqlDAO implements GameDAO {

    public SqlGameDAO() throws DataAccessException {
    }

    @Override
    protected String[] createQuery() {
        String statement = """
                CREATE TABLE IF NOT EXISTS `games` (
                    `gameID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    `gameName` VARCHAR(64) NOT NULL,
                    `whiteUsername` VARCHAR(64),
                    `blackUsername` VARCHAR(64),
                    `game` LONGTEXT NOT NULL
                )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                """;
        return new String[] { statement };
    }

    @Override
    public GameData addGame(GameData gameData) throws DataAccessException {
        String statement = "INSERT INTO games (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?)";
        int id = update(statement, gameData.gameName(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.game());
        return new GameData(id, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(),
                gameData.game());
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE games;";
        update(statement);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String statement = "SELECT * FROM games WHERE gameID = ?;";

        return query(statement, resultSet -> {
            if (resultSet.next()) {
                return parseGame(resultSet);
            }
            return null;
        }, gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String statement = "SELECT * FROM games;";
        return query(statement, resultSet -> {
            Collection<GameData> games = new HashSet<>();

            while (resultSet.next()) {
                games.add(parseGame(resultSet));
            }

            return games;
        });
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        String statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?;";
        update(statement, gameData.whiteUsername(), gameData.blackUsername(), gameData.game(), gameData.gameID());

    }

    private static GameData parseGame(ResultSet resultSet) throws SQLException {
        return new GameData(resultSet.getInt("gameID"), resultSet.getString("whiteUsername"),
                resultSet.getString("blackUsername"), resultSet.getString("gameName"),
                new Gson().fromJson(resultSet.getString("game"), ChessGame.class));
    }
}
