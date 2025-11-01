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

        // Only process CONNECT commands
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
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
                            log.info("WebSocket user authenticated: {}", username);
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
            }
        }

        return message;
    }
}

