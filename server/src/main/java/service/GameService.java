package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.CreateGameRequest;
import model.GameData;
import model.GameList;
import model.UserData;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameList list(String authToken) throws ServerException {
        try {
            auth(authToken);

            return new GameList(dataAccess.getGameDAO().listGames());
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }

    public GameData create(CreateGameRequest gameReq) throws ServerException {
        try {
            auth(gameReq.authToken());

            if (gameReq.gameName() == null)
                throw new ServerException("Game must have a name");
            GameData game = new GameData(0, null, null, gameReq.gameName(), new ChessGame());
            game = dataAccess.getGameDAO().addGame(game);

            return game;
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }

    private AuthData auth(String authToken) throws ServerException {
        try {
            AuthData authData = dataAccess.getAuthDAO().getAuth(authToken);
            if (authData == null)
                throw new ServerException("Invalid auth token");
            return authData;
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }
}