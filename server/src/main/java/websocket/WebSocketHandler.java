package websocket;

import java.io.IOException;
import java.util.Objects;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

import org.eclipse.jetty.websocket.api.Session;

public class WebSocketHandler {
    private DataAccess dataAccess;
    private static final WebSocketHandler instance = new WebSocketHandler();
    private final ConnectionManagement manager = new ConnectionManagement();

    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static WebSocketHandler getInstance() {
        return instance;
    }

    @OnWebSocketMessage
    public void onWebSocketMessage(Session session, String message) throws IOException {
        // log.log("Received message from client: " + session.getRemoteSocketAddress() +
        // ": " + message);

        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);

        AuthData authData;
        GameData gameData;
        try {
            authData = dataAccess.getAuthDAO().getAuth(cmd.getAuthString());

            if (authData == null) {
                manager.error(session, "Error: Invalid token. Unauthorized request");
                return;
            }

            gameData = dataAccess.getGameDAO().getGame(cmd.getGameID());

            if (gameData == null) {
                manager.error(session, "Error: Game does not exist");
                return;
            }
        }
        catch (DataAccessException e) {
            manager.error(session, "Unknown error: " + e.getMessage());
            return;
        }

        switch (cmd.getCommandType()) {
        case JOIN_PLAYER -> joinPlayer(session, cmd, authData.username(), gameData);
        case JOIN_OBSERVER -> joinObserver(session, authData.username(), gameData);
        case MAKE_MOVE -> makeMove(session, cmd, authData.username(), gameData);
        case LEAVE -> leaveGame(session, authData.username(), gameData);
        case RESIGN -> resign(session, authData.username(), gameData);
        }
    }

    private void leaveGame(Session session, String username, GameData gameData) throws IOException {
        manager.remove(gameData.gameID(), session);
        ServerMessage msg = new ServerMessage(ServerMessageType.NOTIFICATION,
                "%s %s".formatted(username,
                        ((Objects.equals(username, gameData.blackUsername())
                                || Objects.equals(username, gameData.whiteUsername())) ? " has left the game"
                                        : " is no longer observing the game")));

        manager.broadcast(gameData.gameID(), new Gson().toJson(msg), null);
    }

    private void joinObserver(Session session, String username, GameData gameData) throws IOException {
        manager.add(gameData.gameID(), session);
        ServerMessage msg = new ServerMessage(gameData.game());
        manager.send(session, new Gson().toJson(msg));

        ServerMessage notification = new ServerMessage(ServerMessageType.NOTIFICATION,
                "%s is now watching the game".formatted(username));

        manager.broadcast(gameData.gameID(), new Gson().toJson(notification), session);
    }

    private void joinPlayer(Session session, UserGameCommand cmd, String username, GameData gameData)
            throws IOException {
        boolean success = false;
        if (cmd.getPlayerColor() == ChessGame.TeamColor.WHITE) {
            success = Objects.equals(gameData.whiteUsername(), username);
        }
        else if (cmd.getPlayerColor() == ChessGame.TeamColor.BLACK) {
            success = Objects.equals(gameData.blackUsername(), username);
        }

        if (!success) {
            manager.error(session, "Error: You are not allowed to join this game");
            return;
        }

        manager.add(gameData.gameID(), session);
        ServerMessage msg = new ServerMessage(gameData.game());
        manager.send(session, new Gson().toJson(msg));

        ServerMessage notification = new ServerMessage(ServerMessageType.NOTIFICATION, "%s joined the game as color %s."
                .formatted(username, ((cmd.getPlayerColor() == ChessGame.TeamColor.WHITE) ? "white" : "black")));

        manager.broadcast(gameData.gameID(), new Gson().toJson(notification), session);
    }

    private void makeMove(Session session, UserGameCommand cmd, String username, GameData gameData) throws IOException {
        if (cmd.getMove() == null) {
            manager.error(session, "Error: No move specified");
            return;
        }

        if (!(Objects.equals(gameData.blackUsername(), username)
                || Objects.equals(gameData.whiteUsername(), username))) {
            manager.error(session, "Error: You are not allowed to make a move in this game");
            return;
        }

        ChessGame game = gameData.game();
        if ((game.getTeamTurn() == ChessGame.TeamColor.WHITE && !Objects.equals(username, gameData.whiteUsername()))
                || (game.getTeamTurn() == ChessGame.TeamColor.BLACK
                        && !Objects.equals(username, gameData.blackUsername()))) {
            manager.error(session, "Error: It is not your turn");
            return;
        }

        try {
            game.makeMove(cmd.getMove());
        }
        catch (InvalidMoveException e) {
            manager.error(session, "Error: That move is invalid");
            return;
        }

        String end = "";

        if (game.isInCheckmate(game.getTeamTurn())) {
            end = "Checkmate! %s won!".formatted(username);
        }
        else if (game.isInStalemate(game.getTeamTurn())) {
            end = "Stalemate!";
        }
        else if (game.isInCheck(game.getTeamTurn())) {
            end = "Check!";
        }

        try {
            dataAccess.getGameDAO().updateGame(gameData);
        }
        catch (DataAccessException e) {
            manager.error(session, "Unknown server error");
            return;
        }

        ServerMessage loadGame = new ServerMessage(game);
        ServerMessage notification = new ServerMessage(ServerMessageType.NOTIFICATION,
                "%s played %s %s".formatted(username, cmd.getMove(), end));

        manager.broadcast(gameData.gameID(), new Gson().toJson(loadGame), null);
        manager.broadcast(gameData.gameID(), new Gson().toJson(notification), session);
    }

    private void resign(Session session, String username, GameData gameData) throws IOException {
        if (!(Objects.equals(gameData.blackUsername(), username)
                || Objects.equals(gameData.whiteUsername(), username))) {
            manager.error(session, "Error: You are not allowed to resign in this game");
            return;
        }

        String opponent = (Objects.equals(username, gameData.whiteUsername())) ? gameData.blackUsername()
                : gameData.whiteUsername();
        try {
            dataAccess.getGameDAO().updateGame(gameData);
        }
        catch (DataAccessException e) {
            manager.error(session, "Unknown server error");
            return;
        }

        ServerMessage notification = new ServerMessage(ServerMessageType.NOTIFICATION,
                "%s resigned, %s won".formatted(username, opponent));
        manager.broadcast(gameData.gameID(), new Gson().toJson(notification), null);

        notification = new ServerMessage(ServerMessageType.NOTIFICATION, "You resigned, %s won".formatted(opponent));
        manager.send(session, new Gson().toJson(notification));
    }

    public void clear() {
        manager.clear();
    }
}
