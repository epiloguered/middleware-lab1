package cn.edu.xmu.chat.demos.web.handler;

import cn.edu.xmu.chat.demos.web.service.UserService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyWebSocketHandler extends TextWebSocketHandler {
    private final UserService userService;

    public MyWebSocketHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage("Please send your user ID"));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = message.getPayload();
        userService.addUser(userId, session);
        // Notify others of the new online user
        for (String otherUserId : userService.getOnlineUsers()) {
            if (!otherUserId.equals(userId)) {
                userService.sendMessageToUser(otherUserId, "用户 " + userId + " 上线");
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = userService.getUserIdBySession(session);
        if (userId != null) {
            userService.removeUserBySession(session);
            // Notify others of the offline user
            for (String otherUserId : userService.getOnlineUsers()) {
                userService.sendMessageToUser(otherUserId, "用户 " + userId + " 离线");
            }
        }
    }
}