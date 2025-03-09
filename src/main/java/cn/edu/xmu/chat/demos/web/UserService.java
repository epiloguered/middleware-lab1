package cn.edu.xmu.chat.demos.web;

import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addUser(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void removeUserBySession(WebSocketSession session) {
        String userId = getUserIdBySession(session);
        if (userId != null) {
            sessions.remove(userId);
        }
    }

    public List<String> getOnlineUsers() {
        return new ArrayList<>(sessions.keySet());
    }

    public void sendMessageToUser(String userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new org.springframework.web.socket.TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUserIdBySession(WebSocketSession session) {
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                return entry.getKey();
            }
        }
        return null;
    }
}