package web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.google.gson.Gson;

import chess.ChessMove;
import ui.Data;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebSocketClient implements MessageHandler.Whole<String> {
    private final WebSocketObserver observer;
    private final Session session;

    public WebSocketClient(WebSocketObserver observer, String host, int port)
            throws URISyntaxException, DeploymentException, IOException {
        this.observer = observer;

        URI uri = new URI("ws://" + host + ":" + port + "/connect");

        session = ContainerProvider.getWebSocketContainer().connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig endpointConfig) {
            }
        }, uri);

        session.addMessageHandler(this);
    }

    @Override
    public void onMessage(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        observer.receiveMessage(serverMessage);
    }

    public void joinGame() throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new UserGameCommand(Data.getInstance().getAuthToken(),
                Data.getInstance().getGameID(), Data.getInstance().getColor())));
    }

    public void observeGame() throws IOException {
        session.getBasicRemote()
                .sendText(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.JOIN_OBSERVER,
                        Data.getInstance().getAuthToken(), Data.getInstance().getGameID())));
    }

    public void move(ChessMove move) throws IOException {
        session.getBasicRemote().sendText(new Gson()
                .toJson(new UserGameCommand(Data.getInstance().getAuthToken(), Data.getInstance().getGameID(), move)));
    }

    public void leave() throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.LEAVE,
                Data.getInstance().getAuthToken(), Data.getInstance().getGameID())));
    }

    public void resign() throws IOException {
        session.getBasicRemote().sendText(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.RESIGN,
                Data.getInstance().getAuthToken(), Data.getInstance().getGameID())));
    }

}
