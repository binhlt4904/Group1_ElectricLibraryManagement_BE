package com.library.librarymanagement.service.notification;

import com.library.librarymanagement.dto.request.SendNotificationRequest;
import com.library.librarymanagement.dto.response.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    // Query APIs (temporary no-op implementations acceptable)
    Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable);
    Page<NotificationDto> getNotificationsByType(Long userId, String type, Pageable pageable);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
    void deleteNotification(Long notificationId);
    List<NotificationDto> getUnreadNotifications(Long userId);
    Long countUnreadNotifications(Long userId);

    // Sending APIs (used by services/controllers)
    void sendNewEventNotification(Long eventId, String eventTitle);
    void sendEventUpdateNotification(Long eventId, String eventTitle, String changedFields);
    void sendLibraryCardStatusNotification(Long userId, String status);
    void sendCardExpiringNotification(Long userId, int daysUntilExpiry);
    void sendBorrowReminderNotification(Long userId, String bookTitle, int daysUntilDue);
    void sendOverdueNotification(Long userId, String bookTitle, int daysOverdue);
    void sendNotification(SendNotificationRequest request);

    // Scheduler stubs (keep ReminderScheduler compiling during rebuild)
    default void sendReminderNotifications() {}
    default void sendOverdueNotifications() {}
}

