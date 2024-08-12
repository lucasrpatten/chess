package websocket.messages;

public class Error extends ServerMessage {

    String errorMessage;

    public Error(String msg) {
        super(ServerMessageType.ERROR);
        this.errorMessage = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
