package com.library.librarymanagement.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    private final NotificationService notificationService;

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
}

