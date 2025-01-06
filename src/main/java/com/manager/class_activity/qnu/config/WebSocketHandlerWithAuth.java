package com.manager.class_activity.qnu.config;

import com.manager.class_activity.qnu.service.AuthenticationService;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.text.ParseException;

public class WebSocketHandlerWithAuth extends WebSocketHandlerDecorator {

    private final AuthenticationService authenticationService;

    public WebSocketHandlerWithAuth(WebSocketHandler delegate, AuthenticationService authenticationService) {
        super(delegate);
        this.authenticationService = authenticationService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Lấy token từ query params
        String query = session.getUri().getQuery();
        String token = query != null && query.startsWith("token=") ? query.substring(6) : null;

        // Xác thực JWT
        if (token == null) {
            session.close();
            throw new IllegalArgumentException("Missing JWT token");
        }
        SignedJWT signedJWT;
        try {
            signedJWT = authenticationService.verifyToken(token);
        } catch (ParseException | IllegalArgumentException e) {
            session.close();
            throw new IllegalArgumentException("Invalid JWT token", e);
        }

        String username = signedJWT.getJWTClaimsSet().getSubject();
        session.getAttributes().put("username", username); // Lưu username vào session
        super.afterConnectionEstablished(session);
    }
}
