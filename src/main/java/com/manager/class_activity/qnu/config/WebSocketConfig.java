package com.manager.class_activity.qnu.config;

import com.manager.class_activity.qnu.service.AuthenticationService;
import com.manager.class_activity.qnu.socket.CustomWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CustomWebSocketHandler webSocketHandler;
    private final AuthenticationService authService;

    public WebSocketConfig(CustomWebSocketHandler webSocketHandler, AuthenticationService authService) {
        this.webSocketHandler = webSocketHandler;
        this.authService = authService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Đăng ký endpoint WebSocket với handler xác thực
        registry.addHandler(new WebSocketHandlerWithAuth(webSocketHandler, authService), "/ws/notifications")
                .setAllowedOrigins("*");
    }
}
