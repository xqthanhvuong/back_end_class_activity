package com.manager.class_activity.qnu.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String username = (String) session.getAttributes().get("username");

        if (username != null) {
            sessions.put(username, session);
            log.info("WebSocket connection established for user: {}", username);
        } else {
            log.warn("Connection without username detected. Closing session.");
            try {
                session.close();
            } catch (Exception e) {
                log.error("Error closing session without username: {}", e.getMessage());
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        // Lấy username từ session attributes
        String username = (String) session.getAttributes().get("username");

        if (username != null) {
            // Xóa session khỏi map
            sessions.remove(username);
            log.info("WebSocket connection closed for user: {}", username);
        }
    }

    // Gửi thông báo tới username cụ thể
    public void sendMessageToUser(String username, String message) {
        WebSocketSession session = sessions.get(username);

        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                log.info("Message sent to user {}: {}", username, message);
            } catch (Exception e) {
                log.error("Error sending message to user {}: {}", username, e.getMessage());
            }
        } else {
            log.warn("No active session found for user {}", username);
        }
    }
}
