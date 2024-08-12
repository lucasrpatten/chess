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

    public void remove(Session session) {
        for (Map.Entry<Integer, Set<Session>> entry : sessions.entrySet()) {
            if (entry.getValue().contains(session)) {
                entry.getValue().remove(session);
                return;
            }
        }
    }

    public void broadcast(Session session, String message) throws IOException {
        // Retrieve the gameID associated with the session
        Integer gameID = getGameIDForSession(session);
        if (gameID == null) {
            throw new IllegalArgumentException("Session is not associated with any game.");
        }

        // Get the set of sessions for the given gameID
        Set<Session> gameSessions = sessions.get(gameID);
        if (gameSessions != null) {
            // Send the message to all sessions in the game, except the provided session
            for (Session s : gameSessions) {
                if (s != session) {
                    send(s, message);
                }
            }
        }
    }

    // Example method to get the gameID for a session
    private Integer getGameIDForSession(Session session) {
        for (Map.Entry<Integer, Set<Session>> entry : sessions.entrySet()) {
            if (entry.getValue().contains(session)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void error(Session session, String message) throws IOException {
        ServerMessage err = new ServerMessage(ServerMessage.ServerMessageType.ERROR, message);
        send(session, new Gson().toJson(err));
    }

    public void send(Session session, String message) throws IOException {

        session.getRemote().sendString(message);

    }

    public void clear() {
        sessions.clear();
    }
}
