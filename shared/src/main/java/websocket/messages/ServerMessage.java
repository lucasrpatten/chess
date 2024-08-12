package websocket.messages;

import java.util.Objects;

import chess.ChessGame;

/**
 * Represents a Message the server can send through a WebSocket Note: You can
 * add to this class, but you should not alter the existing methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    private ChessGame game;
    private String message;
    private String errorMessage;

    public enum ServerMessageType {
        LOAD_GAME, ERROR, NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        switch (type) {
        case ERROR -> errorMessage = message;
        case NOTIFICATION -> this.message = message;
        default -> throw new IllegalArgumentException("Invalid type for message");
        }
    }

    public ServerMessage(ChessGame game) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
    }

    public String getError() {
        return errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public ChessGame getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
