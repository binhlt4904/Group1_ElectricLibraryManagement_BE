package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.NotificationDto;
import com.library.librarymanagement.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Notification Controller
 * Handles notification operations for regular users
 * Endpoints: /api/v1/user/notifications
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/notifications")
@Slf4j
public class UserNotificationController {

    private final NotificationService notificationService;

    /**
     * Get current user's ID from security context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // Extract user ID from authentication
            // Assuming username is the user ID or you have a custom UserDetails implementation
            String username = authentication.getName();
            // You may need to adjust this based on your UserDetails implementation
            try {
                return Long.parseLong(username);
            } catch (NumberFormatException e) {
                log.error("Cannot parse user ID from username: {}", username);
                return null;
            }
        }
        return null;
    }

    /**
     * Get all notifications for current user
     * GET /api/v1/user/notifications
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationDto>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.error("Cannot determine current user ID");
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Fetching notifications for current user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDto> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get notifications for specific user (by user ID)
     * GET /api/v1/user/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationDto>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching notifications for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDto> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for current user
     * GET /api/v1/user/notifications/unread
     */
    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDto>> getMyUnreadNotifications() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.error("Cannot determine current user ID");
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Fetching unread notifications for current user: {}", userId);
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for specific user
     * GET /api/v1/user/notifications/user/{userId}/unread
     */
    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(
            @PathVariable Long userId
    ) {
        log.info("Fetching unread notifications for user: {}", userId);
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread count for current user
     * GET /api/v1/user/notifications/unread-count
     */
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> getMyUnreadCount() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.error("Cannot determine current user ID");
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Counting unread notifications for current user: {}", userId);
        Long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Unread count retrieved successfully")
                        .data(java.util.Map.of("count", count))
                        .build()
        );
    }

    /**
     * Get unread count for specific user
     * GET /api/v1/user/notifications/user/{userId}/unread-count
     */
    @GetMapping("/user/{userId}/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> countUnreadNotifications(
            @PathVariable Long userId
    ) {
        log.info("Counting unread notifications for user: {}", userId);
        Long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Unread count retrieved successfully")
                        .data(java.util.Map.of("count", count))
                        .build()
        );
    }

    /**
     * Mark a notification as read
     * PUT /api/v1/user/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> markAsRead(
            @PathVariable Long notificationId
    ) {
        log.info("Marking notification as read: {}", notificationId);
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("Notification marked as read")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Mark all notifications as read for current user
     * PUT /api/v1/user/notifications/read-all
     */
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> markAllAsReadForCurrentUser() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.error("Cannot determine current user ID");
            return ResponseEntity.badRequest().build();
        }
        
        log.info("Marking all notifications as read for current user: {}", userId);
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("All notifications marked as read")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error marking all notifications as read: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Mark all notifications as read for specific user
     * PUT /api/v1/user/notifications/user/{userId}/read-all
     */
    @PutMapping("/user/{userId}/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> markAllAsRead(
            @PathVariable Long userId
    ) {
        log.info("Marking all notifications as read for user: {}", userId);
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("All notifications marked as read")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error marking all notifications as read: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Delete a notification
     * DELETE /api/v1/user/notifications/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> deleteNotification(
            @PathVariable Long notificationId
    ) {
        log.info("Deleting notification: {}", notificationId);
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("Notification deleted successfully")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error deleting notification: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Get notifications by type for specific user
     * GET /api/v1/user/notifications/user/{userId}/type/{type}
     */
    @GetMapping("/user/{userId}/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationDto>> getNotificationsByType(
            @PathVariable Long userId,
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching notifications of type {} for user: {}", type, userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDto> notifications = notificationService.getNotificationsByType(userId, type, pageable);
        return ResponseEntity.ok(notifications);
    }
}
