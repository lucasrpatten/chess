package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.CreateGameRequest;
import model.GameData;
import model.GameID;
import model.GameList;

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

    public GameID create(String gameName, String authToken) throws ServerException {
        try {
            auth(authToken);
            System.out.println(gameName);
            if (gameName == null)
                throw new BadRequestException("Error: Game must have a name");
            GameData game = new GameData(0, null, null, gameName, new ChessGame());
            game = dataAccess.getGameDAO().addGame(game);

            return new GameID(game.gameID());
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }

    private AuthData auth(String authToken) throws ServerException {
        try {
            AuthData authData = dataAccess.getAuthDAO().getAuth(authToken);
            if (authData == null) {
                throw new UnauthorizedException("Error: Invalid auth token");
            }
            return authData;
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }
}