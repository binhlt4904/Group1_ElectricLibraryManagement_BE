package com.library.librarymanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.security.Principal;

/**
 * Centralized, verbose logging for STOMP/WebSocket lifecycle events to debug real-time notifications.
 */
@Slf4j
@Component
public class WebSocketEventsLogger {

    private final SimpUserRegistry simpUserRegistry;

    public WebSocketEventsLogger(SimpUserRegistry simpUserRegistry) {
        this.simpUserRegistry = simpUserRegistry;
    }

    @EventListener
    public void onSessionConnect(SessionConnectEvent event) {
        Principal principal = extractPrincipal(event.getMessage());
        String sessionId = getSessionId(event.getMessage());
        log.info("🔌 [WS] Session CONNECT: sessionId={}, principal={}", sessionId, principalName(principal));
    }

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        Principal principal = extractPrincipal(event.getMessage());
        String sessionId = getSessionId(event.getMessage());
        log.info("✅ [WS] Session CONNECTED: sessionId={}, principal={}", sessionId, principalName(principal));
        logCurrentUsers();
    }

    @EventListener
    public void onSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("🔌 [WS] Session DISCONNECT: sessionId={}", sessionId);
        logCurrentUsers();
    }

    @EventListener
    public void onSessionSubscribe(SessionSubscribeEvent event) {
        Principal principal = extractPrincipal(event.getMessage());
        String sessionId = getSessionId(event.getMessage());
        String destination = SimpMessageHeaderAccessor.wrap(event.getMessage()).getDestination();
        log.info("📡 [WS] SUBSCRIBE: sessionId={}, principal={}, destination={}", sessionId, principalName(principal), destination);
        logCurrentUsers();
    }

    @EventListener
    public void onSessionUnsubscribe(SessionUnsubscribeEvent event) {
        Principal principal = extractPrincipal(event.getMessage());
        String sessionId = getSessionId(event.getMessage());
        log.info("📴 [WS] UNSUBSCRIBE: sessionId={}, principal={}", sessionId, principalName(principal));
        logCurrentUsers();
    }

    private String principalName(Principal principal) {
        return principal != null ? principal.getName() : "<null>";
    }

    private Principal extractPrincipal(Message<?> message) {
        return SimpMessageHeaderAccessor.wrap(message).getUser();
    }

    private String getSessionId(Message<?> message) {
        return SimpMessageHeaderAccessor.wrap(message).getSessionId();
    }

    private void logCurrentUsers() {
        try {
            StringBuilder sb = new StringBuilder("👥 [WS] Connected users: ");
            boolean first = true;
            for (SimpUser user : simpUserRegistry.getUsers()) {
                if (!first) sb.append(", ");
                sb.append(user.getName());
                first = false;
            }
            log.info(sb.toString());
        } catch (Exception e) {
            log.warn("[WS] Unable to list connected users: {}", e.getMessage());
        }
    }
}
