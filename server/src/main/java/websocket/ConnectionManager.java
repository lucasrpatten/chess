package websocket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import websocket.messages.ErrorMsg;
import websocket.messages.ServerMessage;

public class ConnectionManager {
    private final ConcurrentHashMap<Session, Integer> sessions = new ConcurrentHashMap<>();

    public void add(Session session, int gameID) {
        sessions.put(session, gameID);
    }

    public void remove(Session session) {
        if (sessions.containsKey(session)) {
            sessions.remove(session);
        }
    }

    private int getGameIDForSession(Session session) {
        if (sessions.containsKey(session)) {
            return sessions.get(session);
        }
        return -1;
    }

    public void broadcast(Session session, String message) throws IOException {
        int gameID = getGameIDForSession(session);
        if (gameID == -1) {
            error(session, "Session is not associated with any game.");
            return;
        }

        for (Session s : sessions.keySet()) {
            if (sessions.get(s) == gameID && s != session) {
                send(s, message);
            }
        }

    }

    public void error(Session session, String message) throws IOException {
        ServerMessage err = new ErrorMsg(message);
        send(session, new Gson().toJson(err));
    }

    public void send(Session session, String message) throws IOException {
        session.getRemote().sendString(message);
    }

    public void clear() {
        sessions.clear();
    }
}
