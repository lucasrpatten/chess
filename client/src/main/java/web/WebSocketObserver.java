package web;

import websocket.messages.ServerMessage;

public interface WebSocketObserver {
    void receiveMessage(String message);

}
