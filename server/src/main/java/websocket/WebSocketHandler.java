package websocket;

import java.io.IOException;
import java.util.Objects;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import chess.ChessGame.TeamColor;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import server.Server;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMsg;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.ServerMessageType;

import org.eclipse.jetty.websocket.api.Session;

@WebSocket
public class WebSocketHandler {
    private static final WebSocketHandler instance = new WebSocketHandler();

    public static WebSocketHandler getInstance() {
        return instance;
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
        manager.error(session, new Gson().toJson(new ErrorMsg(message)));
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
    // @OnWebSocketMessage
    // public void onWebSocketMessage(Session session, String message) throws

    // UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);

    // AuthData authData;
    // GameData gameData;
    // try {
    // authData = dataAccess.getAuthDAO().getAuth(cmd.getAuthString());

    // if (authData == null) {
    // manager.error(session, "Error: Invalid token. Unauthorized request");
    // return;
    // }

    // gameData = dataAccess.getGameDAO().getGame(cmd.getGameID());

    // if (gameData == null) {
    // manager.error(session, "Error: Game does not exist");
    // return;
    // }
    // }
    // catch (DataAccessException e) {
    // manager.error(session, "Unknown error: " + e.getMessage());
    // return;
    // }

    // switch (cmd.getCommandType()) {
    // case CONNECT -> join(session, cmd, authData.username(), gameData);
    // case MAKE_MOVE -> makeMove(session, cmd, authData.username(), gameData);
    // case LEAVE -> leaveGame(session, authData.username(), gameData);
    // case RESIGN -> resign(session, authData.username(), gameData);
    // }
    // }

    // private void leaveGame(Session session, String username, GameData gameData)
    // throws IOException {
    // manager.remove(gameData.gameID(), session);
    // ServerMessage msg = new ServerMessage(ServerMessageType.NOTIFICATION,
    // "%s %s".formatted(username,
    // ((Objects.equals(username, gameData.blackUsername())
    // || Objects.equals(username, gameData.whiteUsername())) ? " has left the game"
    // : " is no longer observing the game")));

    // manager.broadcast(gameData.gameID(), new Gson().toJson(msg), null);
    // }

    // private void join(Session session, UserGameCommand cmd, String username,
    // GameData gameData) throws IOException {
    // manager.add(gameData.gameID(), session);
    // if (username.equals(gameData.blackUsername()) ||
    // username.equals(gameData.whiteUsername())) {
    // joinPlayer(session, cmd, username, gameData);
    // return;
    // }
    // joinObserver(session, cmd, username, gameData);
    // }

    // private void joinPlayer(Session session, UserGameCommand cmd, String
    // username, GameData gameData)
    // throws IOException {
    // // try {
    // // AuthData auth = dataAccess.getAuthDAO().getAuth(cmd.getAuthString());
    // // GameData game = dataAccess.getGameDAO().getGame(cmd.getGameID());

    // // TeamColor joining = (cmd.getPlayerColor() == TeamColor.WHITE) ?
    // // TeamColor.BLACK : TeamColor.WHITE;

    // // boolean correctColor;
    // // if (joining == TeamColor.WHITE) {
    // // correctColor = Objects.equals(gameData.whiteUsername(), auth.username());
    // // }
    // // else {
    // // correctColor = Objects.equals(gameData.blackUsername(), auth.username());
    // // }

    // // if (!correctColor) {
    // // manager.error(session, "Error: You are not allowed to join this game");
    // // return;
    // // }

    // // ServerMessage notif = new ServerMessage(ServerMessageType.NOTIFICATION,
    // // "%s joined the game as %s".formatted(auth.username(),
    // // cmd.getPlayerColor().toString()));
    // // manager.broadcast(gameData.gameID(), session, new Gson().toJson(notif));
    // // }
    // // catch (DataAccessException e) {
    // // manager.error(session, "Unknown error: " + e.getMessage());
    // // return;
    // // }

    // manager.add(gameData.gameID(), session);
    // String color = (username.equals(gameData.blackUsername())) ? "white" :
    // "black";
    // ServerMessage msg = new ServerMessage(gameData.game());
    // manager.send(session, new Gson().toJson(msg));
    // ServerMessage notification = new
    // ServerMessage(ServerMessageType.NOTIFICATION,
    // "%s joined the game as %s".formatted(username, color));
    // manager.broadcast(session, new Gson().toJson(notification));

    // // ServerMessage notification = new
    // // ServerMessage(ServerMessageType.NOTIFICATION, "%s joined the game as %s"
    // // .formatted(username, (cmd.getPlayerColor() == TeamColor.WHITE) ? "black" :
    // // "white"));
    // // manager.send(session, new Gson().toJson(notification));
    // }

    // private void joinObserver(Session session, UserGameCommand cmd, String
    // username, GameData gameData)
    // throws IOException {
    // manager.add(gameData.gameID(), session);
    // // ServerMessage notif = new ServerMessage(ServerMessageType.NOTIFICATION,
    // // "%s".formatted(new Gson().toJson(gameData)));
    // // manager.broadcast(session, new Gson().toJson(notif));
    // // String color = (username.equals(gameData.blackUsername())) ? "white" :
    // // "black";
    // ServerMessage msg = new ServerMessage(gameData.game());
    // manager.send(session, msg.getMessage());
    // // ServerMessage msg = new ServerMessage(gameData.game());
    // // manager.send(session, "AMOGUS");
    // // manager.send(session, new Gson().toJson(msg));

    // // ServerMessage msg = new ServerMessage(gameData.game());
    // // manager.send(session, new Gson().toJson(msg));
    // // ServerMessage notification = new
    // // ServerMessage(ServerMessageType.NOTIFICATION,
    // // "%s is now watching the game".formatted(username));

    // // manager.send(session, new Gson().toJson(notification));
    // }

    // // private void joinPlayer(Session session, UserGameCommand cmd, String
    // // username, GameData gameData)
    // // throws IOException {
    // // boolean success = false;
    // // if (cmd.getPlayerColor() == ChessGame.TeamColor.WHITE) {
    // // success = Objects.equals(gameData.whiteUsername(), username);
    // // }
    // // else if (cmd.getPlayerColor() == ChessGame.TeamColor.BLACK) {
    // // success = Objects.equals(gameData.blackUsername(), username);
    // // }

    // // if (!success) {
    // // manager.error(session, "Error: You are not allowed to join this game");
    // // return;
    // // }

    // // manager.add(gameData.gameID(), session);
    // // ServerMessage msg = new ServerMessage(gameData.game());
    // // manager.send(session, new Gson().toJson(msg));

    // // ServerMessage notification = new
    // // ServerMessage(ServerMessageType.NOTIFICATION, "%s joined the game as color
    // // %s."
    // // .formatted(username, ((cmd.getPlayerColor() == ChessGame.TeamColor.WHITE)
    // ?
    // // "white" : "black")));

    // // manager.broadcast(gameData.gameID(), new Gson().toJson(notification),
    // // session);
    // // }

    // private void makeMove(Session session, UserGameCommand cmd, String username,
    // GameData gameData) throws IOException {
    // if (cmd.getMove() == null) {
    // manager.error(session, "Error: No move specified");
    // return;
    // }

    // if (!(Objects.equals(gameData.blackUsername(), username)
    // || Objects.equals(gameData.whiteUsername(), username))) {
    // manager.error(session, "Error: You are not allowed to make a move in this
    // game");
    // return;
    // }

    // ChessGame game = gameData.game();
    // if ((game.getTeamTurn() == ChessGame.TeamColor.WHITE &&
    // !Objects.equals(username, gameData.whiteUsername()))
    // || (game.getTeamTurn() == ChessGame.TeamColor.BLACK
    // && !Objects.equals(username, gameData.blackUsername()))) {
    // manager.error(session, "Error: It is not your turn");
    // return;
    // }

    // try {
    // game.makeMove(cmd.getMove());
    // }
    // catch (InvalidMoveException e) {
    // manager.error(session, "Error: That move is invalid");
    // return;
    // }

    // String end = "";

    // if (game.isInCheckmate(game.getTeamTurn())) {
    // end = "Checkmate! %s won!".formatted(username);
    // }
    // else if (game.isInStalemate(game.getTeamTurn())) {
    // end = "Stalemate!";
    // }
    // else if (game.isInCheck(game.getTeamTurn())) {
    // end = "Check!";
    // }

    // try {
    // dataAccess.getGameDAO().updateGame(gameData);
    // }
    // catch (DataAccessException e) {
    // manager.error(session, "Unknown server error");
    // return;
    // }

    // ServerMessage loadGame = new ServerMessage(game);
    // ServerMessage notification = new
    // ServerMessage(ServerMessageType.NOTIFICATION,
    // "%s played %s %s".formatted(username, cmd.getMove(), end));

    // manager.broadcast(gameData.gameID(), new Gson().toJson(loadGame), null);
    // manager.broadcast(gameData.gameID(), new Gson().toJson(notification),
    // session);
    // }

    // private void resign(Session session, String username, GameData gameData)
    // throws IOException {
    // if (!(Objects.equals(gameData.blackUsername(), username)
    // || Objects.equals(gameData.whiteUsername(), username))) {
    // manager.error(session, "Error: You are not allowed to resign in this game");
    // return;
    // }

    // String opponent = (Objects.equals(username, gameData.whiteUsername())) ?
    // gameData.blackUsername()
    // : gameData.whiteUsername();
    // try {
    // dataAccess.getGameDAO().updateGame(gameData);
    // }
    // catch (DataAccessException e) {
    // manager.error(session, "Unknown server error");
    // return;
    // }

    // ServerMessage notification = new
    // ServerMessage(ServerMessageType.NOTIFICATION,
    // "%s resigned, %s won".formatted(username, opponent));
    // manager.broadcast(gameData.gameID(), new Gson().toJson(notification), null);

    // notification = new ServerMessage(ServerMessageType.NOTIFICATION, "You
    // resigned, %s won".formatted(opponent));
    // manager.send(session, new Gson().toJson(notification));
    // }

    // public void clear() {
    // manager.clear();
    // }
}
