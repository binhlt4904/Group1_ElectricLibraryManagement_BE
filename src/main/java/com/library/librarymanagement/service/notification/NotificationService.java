package com.library.librarymanagement.service.notification;

import com.library.librarymanagement.dto.request.SendNotificationRequest;
import com.library.librarymanagement.dto.response.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    /**
     * Send a notification to a user
     */
    NotificationDto sendNotification(SendNotificationRequest request);

    /**
     * Get all notifications for a user
     */
    Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable);

    /**
     * Get unread notifications for a user
     */
    List<NotificationDto> getUnreadNotifications(Long userId);

    /**
     * Count unread notifications for a user
     */
    Long countUnreadNotifications(Long userId);

    /**
     * Mark a notification as read
     */
    NotificationDto markAsRead(Long notificationId);

    /**
     * Mark all notifications as read for a user
     */
    void markAllAsRead(Long userId);

    /**
     * Get notifications by type for a user
     */
    Page<NotificationDto> getNotificationsByType(Long userId, String type, Pageable pageable);

    /**
     * Delete a notification
     */
    void deleteNotification(Long notificationId);

    /**
     * Send new book notification to all users
     */
    void sendNewBookNotification(Long bookId, String bookTitle);

    /**
     * Send new event notification to all users
     */
    void sendNewEventNotification(Long eventId, String eventTitle);

    /**
     * Send reminder notification for books due in 3 days
     */
    void sendReminderNotifications();

    /**
     * Send overdue notification for books that are overdue
     */
    void sendOverdueNotifications();
}

