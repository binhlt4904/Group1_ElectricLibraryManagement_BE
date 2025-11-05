package com.library.librarymanagement.controller.admin;

import com.library.librarymanagement.dto.request.SendNotificationRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.NotificationDto;
import com.library.librarymanagement.service.notification.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/notifications")
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Send a notification to a user
     * POST /api/v1/admin/notifications/send
     */
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> sendNotification(
            @Valid @RequestBody SendNotificationRequest request
    ) {
        log.info("Sending notification to user: {}", request.getToUserId());
        try {
            NotificationDto notification = notificationService.sendNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.builder()
                            .success(true)
                            .message("Notification sent successfully")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Send new book notification to all users
     * POST /api/v1/admin/notifications/new-book
     */
    @PostMapping("/new-book")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> sendNewBookNotification(
            @RequestParam Long bookId,
            @RequestParam String bookTitle
    ) {
        log.info("Sending new book notification for book: {}", bookTitle);
        try {
            notificationService.sendNewBookNotification(bookId, bookTitle);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("New book notification sent to all users")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error sending new book notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Send new event notification to all users
     * POST /api/v1/admin/notifications/new-event
     */
    @PostMapping("/new-event")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> sendNewEventNotification(
            @RequestParam Long eventId,
            @RequestParam String eventTitle
    ) {
        log.info("Sending new event notification for event: {}", eventTitle);
        try {
            notificationService.sendNewEventNotification(eventId, eventTitle);
            return ResponseEntity.ok(
                    ApiResponse.builder()
                            .success(true)
                            .message("New event notification sent to all users")
                            .build()
            );
        } catch (Exception e) {
            log.error("Error sending new event notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Get all notifications for a user
     * GET /api/v1/admin/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
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
     * Get unread notifications for a user
     * GET /api/v1/admin/notifications/user/{userId}/unread
     */
    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications(
            @PathVariable Long userId
    ) {
        log.info("Fetching unread notifications for user: {}", userId);
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Count unread notifications for a user
     * GET /api/v1/admin/notifications/user/{userId}/unread-count
     */
    @GetMapping("/user/{userId}/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
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
     * PUT /api/v1/admin/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Mark all notifications as read for a user
     * PUT /api/v1/admin/notifications/user/{userId}/read-all
     */
    @PutMapping("/user/{userId}/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Get notifications by type for a user
     * GET /api/v1/admin/notifications/user/{userId}/type/{type}
     */
    @GetMapping("/user/{userId}/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
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

    /**
     * Delete a notification
     * DELETE /api/v1/admin/notifications/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }
}

