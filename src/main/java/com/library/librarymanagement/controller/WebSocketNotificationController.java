package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.response.NotificationDto;
import com.library.librarymanagement.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle WebSocket connection
     * Client sends: /app/notifications/connect
     */
    @MessageMapping("/notifications/connect")
    public void handleConnect(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        if (principal != null) {
            log.info("User connected to notifications: {}", principal.getName());
            headerAccessor.getSessionAttributes().put("username", principal.getName());
        }
    }

    /**
     * Handle WebSocket disconnection
     * Client sends: /app/notifications/disconnect
     */
    @MessageMapping("/notifications/disconnect")
    public void handleDisconnect(SimpMessageHeaderAccessor headerAccessor) {
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("User disconnected from notifications: {}", username);
        }
    }

    /**
     * Get unread notifications for the connected user
     * Client sends: /app/notifications/unread
     */
    @MessageMapping("/notifications/unread")
    @SendTo("/topic/notifications/unread")
    public List<NotificationDto> getUnreadNotifications(Principal principal) {
        if (principal != null) {
            log.info("Fetching unread notifications for user: {}", principal.getName());
            // Note: In a real implementation, you would fetch the user ID from the database
            // For now, this is a placeholder
            return List.of();
        }
        return List.of();
    }

    /**
     * Mark notification as read via WebSocket
     * Client sends: /app/notifications/mark-read
     */
    @MessageMapping("/notifications/mark-read")
    public void markNotificationAsRead(
            @Payload Long notificationId,
            Principal principal
    ) {
        if (principal != null) {
            log.info("Marking notification as read: {} by user: {}", notificationId, principal.getName());
            try {
                notificationService.markAsRead(notificationId);
                // Broadcast to all connected users
                messagingTemplate.convertAndSend(
                        "/topic/notifications/marked-read",
                        "Notification " + notificationId + " marked as read"
                );
            } catch (Exception e) {
                log.error("Error marking notification as read: {}", e.getMessage());
            }
        }
    }

    /**
     * Broadcast notification to a specific user
     * This is called internally by the NotificationService
     */
    public void broadcastNotificationToUser(Long userId, NotificationDto notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notification
            );
            log.info("Notification broadcasted to user: {}", userId);
        } catch (Exception e) {
            log.warn("Failed to broadcast notification to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Broadcast notification to all connected users
     */
    public void broadcastNotificationToAll(NotificationDto notification) {
        try {
            messagingTemplate.convertAndSend(
                    "/topic/notifications/all",
                    notification
            );
            log.info("Notification broadcasted to all users");
        } catch (Exception e) {
            log.warn("Failed to broadcast notification to all users: {}", e.getMessage());
        }
    }
}

