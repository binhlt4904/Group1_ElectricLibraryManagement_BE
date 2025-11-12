package com.library.librarymanagement.service.notification;

import com.library.librarymanagement.entity.LibraryCard;
import com.library.librarymanagement.repository.LibraryCardRepository;
import com.library.librarymanagement.service.event.EventService;
import com.library.librarymanagement.service.notification.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final NotificationServiceImpl notificationService;
    private final EventService eventService;
    private final LibraryCardRepository libraryCardRepository;

    /**
     * Send reminder notifications for books due in 3 days
     * Runs daily at 2:00 AM (02:00:00)
     * Cron expression: 0 0 2 * * * (second, minute, hour, day, month, day-of-week)
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendReminderNotifications() {
        log.info("Starting scheduled task: Send reminder notifications for books due in 3 days");
        try {
            notificationService.sendReminderNotifications();
            log.info("Reminder notifications sent successfully");
        } catch (Exception e) {
            log.error("Error sending reminder notifications: {}", e.getMessage(), e);
        }
    }

    /**
     * Send overdue notifications for books that are overdue
     * Runs daily at 2:15 AM (02:15:00)
     * Cron expression: 0 15 2 * * * (second, minute, hour, day, month, day-of-week)
     */
    @Scheduled(cron = "0 15 2 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendOverdueNotifications() {
        log.info("Starting scheduled task: Send overdue notifications");
        try {
            notificationService.sendOverdueNotifications();
            log.info("Overdue notifications sent successfully");
        } catch (Exception e) {
            log.error("Error sending overdue notifications: {}", e.getMessage(), e);
        }
    }

    /**
     * Auto-update event status based on current date/time
     * Runs every 30 minutes to update event statuses: upcoming -> ongoing -> completed
     * Cron expression: 0 (slash)30 * * * * (second, minute, hour, day, month, day-of-week)
     */
    @Scheduled(cron = "0 */30 * * * *", zone = "Asia/Ho_Chi_Minh")
    public void autoUpdateEventStatus() {
        log.info("Starting scheduled task: Auto-update event status");
        try {
            eventService.autoUpdateEventStatus();
            log.info("Event status auto-update completed successfully");
        } catch (Exception e) {
            log.error("Error in auto-update event status: {}", e.getMessage(), e);
        }
    }

    /**
     * Send library card expiry notifications
     * Runs daily at 3:00 AM (03:00:00)
     * Sends notifications for cards expiring within 7 days
     * Cron expression: 0 0 3 * * * (second, minute, hour, day, month, day-of-week)
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendLibraryCardExpiryNotifications() {
        log.info("Starting scheduled task: Send library card expiry notifications");
        try {
            // Calculate date 7 days from now
            Calendar calendar = Calendar.getInstance();
            Date today = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            Date sevenDaysFromNow = calendar.getTime();

            // Find all library cards expiring within 7 days
            List<LibraryCard> expiringCards = libraryCardRepository.findAll().stream()
                    .filter(card -> card.getExpiryDate() != null &&
                            card.getExpiryDate().after(today) &&
                            card.getExpiryDate().before(sevenDaysFromNow) &&
                            "ACTIVE".equalsIgnoreCase(card.getStatus()))
                    .collect(Collectors.toList());

            log.info("📧 Found {} library cards expiring within 7 days", expiringCards.size());

            for (LibraryCard card : expiringCards) {
                try {
                    if (card.getReader() != null && card.getReader().getAccount() != null) {
                        Long userId = card.getReader().getAccount().getId();
                        
                        // Calculate days until expiry
                        long daysUntilExpiry = (card.getExpiryDate().getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
                        
                        // Send WebSocket notification
                        notificationService.sendCardExpiringNotification(userId, (int) daysUntilExpiry);
                        log.info("🔔 Sent card expiry notification to user ID: {} (expires in {} days)", userId, daysUntilExpiry);
                    }
                } catch (Exception e) {
                    log.warn("⚠️ Failed to send card expiry notification: {}", e.getMessage());
                }
            }

            log.info("Library card expiry notifications sent successfully");
        } catch (Exception e) {
            log.error("Error sending library card expiry notifications: {}", e.getMessage(), e);
        }
    }
}

