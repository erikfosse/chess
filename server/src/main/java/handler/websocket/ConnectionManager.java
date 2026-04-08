package handler.websocket;

import model.JsonSerialization;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<Session, Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        if (connections.get(gameID)==null) {
            var sessionMap = new ConcurrentHashMap<Session, Session>();
            sessionMap.put(session, session);
            connections.put(gameID, sessionMap);
        } else {
            connections.get(gameID).put(session, session);
        }
    }

    public void remove(Integer gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void exclusiveBroadcast(Session excludeSession, ServerMessage message, int gameID) throws IOException {
        String msg = JsonSerialization.toJson(message);
        var sessions = connections.get(gameID);
        for (Session c : sessions.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }

    public void inclusiveBroadcast(Session excludeSession, ServerMessage message, int gameID) throws IOException {
        String msg = JsonSerialization.toJson(message);
        var sessions = connections.get(gameID);
        for (Session c : sessions.values()) {
            if (c.isOpen()) {
                if (c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
