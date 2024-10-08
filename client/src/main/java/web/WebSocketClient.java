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
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

public class WebSocketClient implements MessageHandler.Whole<String> {
    private final WebSocketObserver observer;
    private final Session session;

    public WebSocketClient(WebSocketObserver ui, String host, int port)
            throws URISyntaxException, DeploymentException, IOException {
        this.observer = ui;

        URI uri = new URI("ws://" + host + ":" + port + "/ws");

        session = ContainerProvider.getWebSocketContainer().connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig endpointConfig) {
            }
        }, uri);

        session.addMessageHandler(this);
    }

    @Override
    public void onMessage(String message) {
        observer.receiveMessage(message);
    }

    private void sendText(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    public void connect() throws IOException {
        String authToken = Data.getInstance().getAuthToken();
        int gameID = Data.getInstance().getGameID();
        sendText(new Gson().toJson(new UserGameCommand(CommandType.CONNECT, authToken, gameID)));
    }

    public void move(ChessMove move) throws IOException {
        MakeMove moveCmd = new MakeMove(Data.getInstance().getAuthToken(), Data.getInstance().getGameID(), move);
        sendText(new Gson().toJson(moveCmd));
    }

    public void leave() throws IOException {
        sendText(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.LEAVE,
                Data.getInstance().getAuthToken(), Data.getInstance().getGameID())));
    }

    public void resign() throws IOException {
        sendText(new Gson().toJson(new UserGameCommand(UserGameCommand.CommandType.RESIGN,
                Data.getInstance().getAuthToken(), Data.getInstance().getGameID())));
    }
}
