package web;

import websocket.messages.ServerMessage;

public interface WebSocketObserver {
    void receiveMessage(ServerMessage message);
}
