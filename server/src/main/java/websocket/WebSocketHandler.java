package websocket;

import java.io.IOException;
import java.util.Objects;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import chess.ChessBoard;
import chess.ChessMove;
import chess.InvalidMoveException;
import chess.ChessGame.TeamColor;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMsg;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import org.eclipse.jetty.websocket.api.Session;

@WebSocket
public class WebSocketHandler {
    private static final WebSocketHandler INSTANCE = new WebSocketHandler();

    public static WebSocketHandler getInstance() {
        return INSTANCE;
    }

    private DataAccess dataAccess;

    private final ConnectionManager manager = new ConnectionManager();

    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        manager.add(session, 0);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        manager.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        DataPair dataPair = getData(session, cmd);
        if (dataPair == null) {
            return;
        }
        switch (cmd.getCommandType()) {
        case CONNECT:
            connect(session, cmd, dataPair);
            break;
        case MAKE_MOVE:
            MakeMove moveCmd = new Gson().fromJson(message, MakeMove.class);
            makeMove(session, moveCmd, dataPair);
            break;
        case RESIGN:
            resign(session, cmd, dataPair);
            break;
        case LEAVE:
            leave(session, cmd, dataPair);
            break;
        default:
            break;
        }
    }

    private DataPair getData(Session session, UserGameCommand cmd) throws IOException {
        AuthData authData;
        GameData gameData;
        try {
            authData = dataAccess.getAuthDAO().getAuth(cmd.getAuthToken());

            if (authData == null) {
                sendError(session, "Error: Invalid token. Unauthorized request");
                return null;
            }

            gameData = dataAccess.getGameDAO().getGame(cmd.getGameID());
            if (gameData == null) {
                sendError(session, "Error: Game does not exist.");
                return null;
            }
        }
        catch (DataAccessException e) {
            sendError(session, "Error: Invalid Request");
            return null;
        }
        return new DataPair(authData, gameData);
    }

    private void sendError(Session session, String message) throws IOException {
        manager.error(session, message);
    }

    private void connect(Session session, UserGameCommand cmd, DataPair dataPair) throws IOException {
        Notification notification;
        String username = dataPair.getAuthData().username();
        GameData gameData = dataPair.getGameData();

        manager.add(session, gameData.gameID());

        LoadGame loadGame = new LoadGame(gameData.game());
        manager.send(session, new Gson().toJson(loadGame));

        TeamColor joinColor = getTeamColor(username, gameData);
        if (joinColor != null) {
            notification = new Notification(
                    "%s has joined the game as %s.".formatted(username, joinColor.toString().toLowerCase()));
        }
        else {
            notification = new Notification("%s is now observing the game.".formatted(username));
        }

        manager.broadcast(session, new Gson().toJson(notification));
    }

    private void makeMove(Session session, MakeMove cmd, DataPair dataPair) throws IOException {
        String username = dataPair.getAuthData().username();
        GameData gameData = dataPair.getGameData();
        TeamColor userColor = getTeamColor(username, gameData);
        ChessMove move = cmd.getMove();

        if (userColor == null) {
            sendError(session, "Error: You are not playing in this game.");
            return;
        }
        if (gameData.game().getGameOver()) {
            sendError(session, "Error: Game is over. No more moves can be made.");
            return;
        }

        TeamColor opponent = (userColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);

        if (gameData.game().getTeamTurn().equals(opponent)) {
            sendError(session, "Error: It is not your turn.");
            return;
        }

        ChessBoard board = gameData.game().getBoard();
        if (board.getPiece(move.getStartPosition()) == null) {
            sendError(session, "Error: You are trying to move a piece that does not exist.");
            return;
        }

        if (board.getPiece(move.getStartPosition()).getTeamColor().equals(opponent)) {
            sendError(session, "Error: You can only move your own pieces.");
            return;
        }

        try {
            gameData.game().makeMove(move);
            Notification notif;

            if (gameData.game().isInCheckmate(opponent)) {
                notif = new Notification("Checkmate! %s is the winner.".formatted(opponent.toString().toLowerCase()));
                gameData.game().setGameOver(true);
            }
            else if (gameData.game().isInStalemate(opponent)) {
                notif = new Notification("Stalemate caused by %s. Game ends with a tie!".formatted(username));
                gameData.game().setGameOver(true);
            }
            else if (gameData.game().isInCheck(opponent)) {
                notif = new Notification("%s is in check.".formatted(opponent.toString().toLowerCase()));
            }
            else {
                notif = new Notification("%s has made a move.".formatted(username));
            }

            manager.broadcast(session, new Gson().toJson(notif));

            dataAccess.getGameDAO().updateGame(gameData);

            broadcastGame(session, gameData);
            sendGame(session, gameData);
        }
        catch (InvalidMoveException e) {
            sendError(session, "That is not a valid move.");
            return;
        }
        catch (DataAccessException e) {
            sendError(session, "Error: Invalid Request");
            return;
        }
    }

    private void resign(Session session, UserGameCommand cmd, DataPair dataPair) throws IOException {
        String username = dataPair.getAuthData().username();
        GameData gameData = dataPair.getGameData();
        TeamColor userColor = getTeamColor(username, gameData);
        String opponentUsername = (userColor == TeamColor.WHITE ? gameData.blackUsername() : gameData.whiteUsername());

        if (userColor == null) {
            sendError(session, "Error: You are not playing in this game.");
            return;
        }

        if (gameData.game().getGameOver()) {
            sendError(session, "Error: Game is over. No more moves can be made.");
            return;
        }

        gameData.game().setGameOver(true);
        try {
            dataAccess.getGameDAO().updateGame(gameData);
        }
        catch (DataAccessException e) {
            sendError(session, "Error: Invalid Request");
            return;
        }
        Notification notif = new Notification(
                "%s has resigned, %s is the winner!".formatted(username, opponentUsername));
        manager.broadcast(session, new Gson().toJson(notif));
        manager.send(session, new Gson().toJson(notif));
    }

    private void leave(Session session, UserGameCommand cmd, DataPair dataPair) throws IOException {
        String username = dataPair.getAuthData().username();
        TeamColor userColor = getTeamColor(username, dataPair.getGameData());
        GameData gameData = dataPair.getGameData();
        Notification notification = new Notification("%s has left the game.".formatted(username));
        manager.broadcast(session, new Gson().toJson(notification));

        if (userColor.equals(TeamColor.WHITE)) {
            gameData = gameData.setWhiteUsername(null);
        }
        else if (userColor.equals(TeamColor.BLACK)) {
            gameData = gameData.setBlackUsername(null);
        }
        try {
            dataAccess.getGameDAO().updateGame(gameData);
        }
        catch (DataAccessException e) {
            sendError(session, "Error: Invalid Request");
            return;
        }
        manager.remove(session);
        session.close();

    }

    private void broadcastGame(Session session, GameData gameData) throws IOException {
        LoadGame loadGame = new LoadGame(gameData.game());
        manager.broadcast(session, new Gson().toJson(loadGame));
    }

    private void sendGame(Session session, GameData gameData) throws IOException {
        LoadGame loadGame = new LoadGame(gameData.game());
        manager.send(session, new Gson().toJson(loadGame));
    }

    private TeamColor getTeamColor(String username, GameData gameData) {
        if (username.equals(gameData.whiteUsername())) {
            return TeamColor.WHITE;
        }
        if (username.equals(gameData.blackUsername())) {
            return TeamColor.BLACK;
        }
        return null;

    }
}