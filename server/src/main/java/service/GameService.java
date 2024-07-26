package service;

import chess.ChessGame;
import chess.ChessGame.TeamColor;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.CreateGameResult;
import model.GameListResult;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * Get a list of all games
     * 
     * @param authToken The auth token
     * @return The list of games
     * @throws ServerException if there is an error
     */
    public GameListResult list(String authToken) throws ServerException {
        try {
            auth(authToken);

            return new GameListResult(dataAccess.getGameDAO().listGames());
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Create a new game
     * 
     * @param gameName  The name of the game
     * @param authToken The auth token
     * @return The game ID
     * @throws ServerException if there is an error
     */
    public CreateGameResult create(String gameName, String authToken) throws ServerException {
        try {
            auth(authToken);
            if (gameName == null)
                throw new BadRequestException("Error: Game must have a name");
            GameData game = new GameData(0, null, null, gameName, new ChessGame());
            game = dataAccess.getGameDAO().addGame(game);

            return new CreateGameResult(game.gameID());
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Join a game
     * 
     * @param color     The color of the player joining
     * @param gameID    The ID of the game to join
     * @param authToken The auth token of the player
     * @return null
     * @throws ServerException if there is an error
     */
    public Object join(TeamColor color, int gameID, String authToken) throws ServerException {
        try {
            String username = auth(authToken).username();
            GameData game = dataAccess.getGameDAO().getGame(gameID);
            if (game == null) {
                throw new BadRequestException("Error: Game you are joining is not available");
            }
            if (color == null) {
                throw new BadRequestException("Error: You must select a color");
            }
            if ((color == TeamColor.BLACK && game.blackUsername() != null)
                    || (color == TeamColor.WHITE && game.whiteUsername() != null)) {
                throw new AlreadyTakenException("Error: Color is already taken");
            }

            if (color == TeamColor.BLACK) {
                dataAccess.getGameDAO().updateGame(game.setBlackUsername(username));
            }
            else {
                dataAccess.getGameDAO().updateGame(game.setWhiteUsername(username));
            }

            return null;
        }
        catch (DataAccessException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Verify an auth token
     * 
     * @param authToken the auth token
     * @return the auth data
     * @throws ServerException if there is an error
     */
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