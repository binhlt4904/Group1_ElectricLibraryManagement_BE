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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Chỉ xử lý MỘT LẦN DUY NHẤT tại lệnh CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String username = jwtService.extractUsername(token);
                    if (username != null) {
                        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                        if (jwtService.isValidToken(token, userDetails)) {
                            // Tạo Authentication và gán nó vào session WebSocket
                            Authentication auth = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                            accessor.setUser(auth); // Dòng này là đủ để Spring duy trì Principal
                            log.info("✅ [WebSocket Auth] Principal set for user: {}", username);
                        } else {
                            log.warn("⚠️ [WebSocket Auth] Invalid token provided.");
                            // Không cần throw exception, Spring sẽ tự từ chối kết nối nếu Principal là null
                        }
                    }
                } catch (Exception e) {
                    log.error("❌ [WebSocket Auth] Error during authentication: {}", e.getMessage());
                }
            } else {
                log.warn("⚠️ [WebSocket Auth] No Authorization header on CONNECT.");
            }
        }
        
        // Không cần kiểm tra SUBSCRIBE ở đây nữa.
        // Spring sẽ tự động xử lý việc gắn Principal vào các message tiếp theo.
        return message;
    }
}


