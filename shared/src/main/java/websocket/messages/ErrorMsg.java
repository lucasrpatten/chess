package websocket.messages;

public class ErrorMsg extends ServerMessage {

    String errorMessage;

    public ErrorMsg(String msg) {
        super(ServerMessageType.ERROR);
        this.errorMessage = msg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
