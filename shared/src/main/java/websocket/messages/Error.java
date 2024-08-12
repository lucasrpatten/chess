package websocket.messages;

public class Error extends ServerMessage {

    String message;

    public Error(String msg) {
        super(ServerMessageType.ERROR);
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }
}
