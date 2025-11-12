package com.library.librarymanagement.config;

import com.library.librarymanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * WebSocket authentication interceptor for JWT token validation
 * Validates JWT tokens on WebSocket CONNECT commands
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Only process CONNECT commands (not HTTP handshake requests)
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.debug("Processing STOMP CONNECT command");
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null) {
                authHeader = accessor.getFirstNativeHeader("authorization");
            }

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                log.debug("Authorization header found in STOMP CONNECT");
                String token = authHeader.substring(7);

                try {
                    // Validate token and extract username
                    String username = jwtService.extractUsername(token);

                    if (username != null) {
                        // Create a simple UserDetails object for validation
                        UserDetails userDetails = User.builder()
                                .username(username)
                                .password("")
                                .authorities(new ArrayList<>())
                                .build();

                        // Validate token
                        if (jwtService.isValidToken(token, userDetails)) {
                            // Create authentication token
                            Authentication auth = new UsernamePasswordAuthenticationToken(
                                    username, null, new ArrayList<>()
                            );
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            accessor.setUser(auth);
                            log.info("✅ [WebSocket Auth] User authenticated and Principal set:");
                            log.info("   → Username (Principal name): {}", username);
                            log.info("   → Will receive messages at: /user/{}/queue/notifications", username);
                        } else {
                            log.warn("Invalid token for user: {}", username);
                            accessor.setUser(null);
                        }
                    }
                } catch (Exception e) {
                    // Invalid token - connection will be rejected
                    log.error("WebSocket authentication error: {}", e.getMessage());
                    accessor.setUser(null);
                }
            } else {
                log.warn("No Authorization header found in STOMP CONNECT frame");
            }
        }

        // Log subscriptions to verify destination and principal
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            String principal = accessor.getUser() != null ? accessor.getUser().getName() : null;
            log.info("[WebSocket] SUBSCRIBE: destination={}, principal={}", destination, principal);
        }

        return message;
    }
}

