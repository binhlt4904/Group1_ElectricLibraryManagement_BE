package com.library.librarymanagement.service.borrow;

import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.repository.borrow.BorrowRepository;
import com.library.librarymanagement.service.email.EmailService;
import com.library.librarymanagement.service.notification.NotificationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowScheduleService {
    private final BorrowRepository borrowRepository;
    private final EmailService mailService;
    private final NotificationServiceImpl notificationService;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyBorrowCheck() {
        Date today = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfTomorrow = cal.getTime();

        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date endOfTomorrow = cal.getTime();

        List<BorrowRecord> dueTomorrow = borrowRepository.findRecordsDueTomorrow(startOfTomorrow, endOfTomorrow);
        if (!dueTomorrow.isEmpty()) {
            log.info("📧 Found {} borrow records due tomorrow", dueTomorrow.size());
            for (BorrowRecord record : dueTomorrow) {
                String email = record.getLibraryCard()
                        .getReader()
                        .getAccount()
                        .getEmail();

                String bookTitle = record.getBook().getTitle();
                String dueDate = sdf.format(record.getAllowedDate());
                String readerName = record.getLibraryCard()
                        .getReader()
                        .getAccount()
                        .getFullName();
                Long userId = record.getLibraryCard()
                        .getReader()
                        .getAccount()
                        .getId();

                String subject = "Remind for returning book - " + bookTitle;
                String message = String.format(
                        "Dear %s,\n\nBook \"%s\", you borrowed, will be over due on  %s.\n" +
                                "Please access electric library to return on time.\n\nSincerely,\nFPT Electric Library.",
                        readerName, bookTitle, dueDate
                );

                try {
                    mailService.sendMailToReminder(email, subject, message);
                    log.info("📨 Sent email reminder to {}", email);
                } catch (Exception e) {
                    log.warn("⚠️ Failed to send mail to {}: {}", email, e.getMessage());
                }

                // Send WebSocket notification
                try {
                    notificationService.sendBorrowReminderNotification(userId, bookTitle, 1);
                    log.info("🔔 Sent WebSocket reminder notification to user ID: {}", userId);
                } catch (Exception e) {
                    log.warn("⚠️ Failed to send WebSocket reminder to user {}: {}", userId, e.getMessage());
                }
            }
        }

        int updatedCount = borrowRepository.markOverdueRecords(today);

        if (updatedCount > 0) {
            log.info("✅ Scheduler: Updated {} overdue borrow records at {}", updatedCount, today);
            
            // Send overdue notifications for newly marked overdue records
            try {
                sendOverdueNotifications(today);
            } catch (Exception e) {
                log.error("Error sending overdue notifications: {}", e.getMessage());
            }
        } else {
            log.info("ℹ️ Scheduler: No overdue borrow records to update today ({})", today);
        }
    }

    @Transactional
    public void sendOverdueNotifications(Date today) {
        // Find all overdue records
        List<BorrowRecord> overdueRecords = borrowRepository.findAll().stream()
                .filter(record -> "OVERDUE".equalsIgnoreCase(record.getStatus()) && 
                        record.getReturnRecord() == null)
                .collect(Collectors.toList());

        log.info("📧 Found {} overdue borrow records", overdueRecords.size());
        
        for (BorrowRecord record : overdueRecords) {
            try {
                String bookTitle = record.getBook().getTitle();
                Long userId = record.getLibraryCard()
                        .getReader()
                        .getAccount()
                        .getId();
                
                // Calculate days overdue
                long daysOverdue = (today.getTime() - record.getAllowedDate().getTime()) / (1000 * 60 * 60 * 24);
                
                // Send WebSocket notification
                notificationService.sendOverdueNotification(userId, bookTitle, (int) daysOverdue);
                log.info("🔔 Sent overdue notification to user ID: {} for book: {}", userId, bookTitle);
            } catch (Exception e) {
                log.warn("⚠️ Failed to send overdue notification: {}", e.getMessage());
            }
        }
    }
}
