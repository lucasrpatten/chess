package websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import websocket.messages.ServerMessage;

public class ConnectionManagement {
    private final Map<Integer, Set<Session>> sessions = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        if (!sessions.containsKey(gameID)) {
            sessions.put(gameID, Collections.synchronizedSet(new HashSet<>()));
        }
    }

    public void remove(int gameID, Session session) {
        if (sessions.containsKey(gameID)) {
            sessions.get(gameID).remove(session);
        }
    }

    public void broadcast(int gameID, String message, Session session) throws IOException {
        for (Session s : sessions.get(gameID)) {
            if (s != session) {
                send(s, message);
            }
        }
    }

    public void error(Session session, String message) throws IOException {
        ServerMessage err = new ServerMessage(ServerMessage.ServerMessageType.ERROR, message);
        send(session, new Gson().toJson(err));
    }

    public void send(Session session, String message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }

    public void clear() {
        sessions.clear();
    }
}
