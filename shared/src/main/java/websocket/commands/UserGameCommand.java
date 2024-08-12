package websocket.commands;

import java.util.Objects;

import chess.ChessGame;
import chess.ChessMove;

/**
 * Represents a command a user can send the server over a websocket Note: You
 * can add to this class, but you should not alter the existing methods.
 */

public class UserGameCommand {

    public UserGameCommand(CommandType type, String authToken, int gameID) {
        commandType = type;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public UserGameCommand(String authToken, int gameID, ChessMove move) {
        this(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public UserGameCommand(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        this(CommandType.JOIN_PLAYER, authToken, gameID);
        this.playerColor = playerColor;
    }

    public enum CommandType {
        JOIN_PLAYER, JOIN_OBSERVER, MAKE_MOVE, LEAVE, RESIGN, CONNECT
    }

    protected CommandType commandType;

    private final String authToken;
    private Integer gameID;
    private ChessGame.TeamColor playerColor;
    private ChessMove move;

    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public ChessMove getMove() {
        return move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }
}