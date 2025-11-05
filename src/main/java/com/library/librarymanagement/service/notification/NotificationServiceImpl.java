package com.library.librarymanagement.service.notification;

import com.library.librarymanagement.dto.request.SendNotificationRequest;
import com.library.librarymanagement.dto.response.NotificationDto;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.BorrowRecord;
import com.library.librarymanagement.entity.Notification;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.repository.admin.BorrowRecordRepository;
import com.library.librarymanagement.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public NotificationDto sendNotification(SendNotificationRequest request) {
        Account toUser = accountRepository.findById(request.getToUserId())
                .orElseThrow(() -> new ObjectNotExistException("User not found with ID: " + request.getToUserId()));

        Notification notification = Notification.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .notificationType(request.getNotificationType())
                .toUser(toUser)
                .isRead(false)
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .relatedBookId(request.getRelatedBookId())
                .relatedEventId(request.getRelatedEventId())
                .relatedBorrowRecordId(request.getRelatedBorrowRecordId())
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        sendWebSocketNotification(toUser.getId(), mapToDto(savedNotification));

        log.info("Notification sent to user: {}", toUser.getUsername());
        return mapToDto(savedNotification);
    }

    @Override
    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotExistException("User not found with ID: " + userId));

        return notificationRepository.findByToUserOrderByCreatedDateDesc(user, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotExistException("User not found with ID: " + userId));

        return notificationRepository.findUnreadNotifications(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long countUnreadNotifications(Long userId) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotExistException("User not found with ID: " + userId));

        return notificationRepository.countUnreadNotifications(user);
    }

    @Override
    public NotificationDto markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ObjectNotExistException("Notification not found with ID: " + notificationId));

        notification.setIsRead(true);
        notification.setReadDate(new Timestamp(System.currentTimeMillis()));
        Notification updated = notificationRepository.save(notification);

        return mapToDto(updated);
    }

    @Override
    public void markAllAsRead(Long userId) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotExistException("User not found with ID: " + userId));

        List<Notification> unreadNotifications = notificationRepository.findUnreadNotifications(user);
        Timestamp now = new Timestamp(System.currentTimeMillis());

        unreadNotifications.forEach(n -> {
            n.setIsRead(true);
            n.setReadDate(now);
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    public Page<NotificationDto> getNotificationsByType(Long userId, String type, Pageable pageable) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotExistException("User not found with ID: " + userId));

        return notificationRepository.findByTypeForUser(user, type, pageable)
                .map(this::mapToDto);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ObjectNotExistException("Notification not found with ID: " + notificationId));

        notificationRepository.delete(notification);
    }

    @Override
    public void sendNewBookNotification(Long bookId, String bookTitle) {
        // Get all users and send notification
        List<Account> allUsers = accountRepository.findAll();
        log.info("📘 Sending new book notification to {} users for book: {}", allUsers.size(), bookTitle);

        int successCount = 0;
        allUsers.forEach(user -> {
            Notification notification = Notification.builder()
                    .title("New Book Available")
                    .description("A new book '" + bookTitle + "' has been added to the library")
                    .notificationType("NEW_BOOK")
                    .toUser(user)
                    .isRead(false)
                    .createdDate(new Timestamp(System.currentTimeMillis()))
                    .relatedBookId(bookId)
                    .build();

            Notification saved = notificationRepository.save(notification);
            log.info("✅ Notification saved to DB with ID: {} for user: {}", saved.getId(), user.getUsername());
            
            sendWebSocketNotification(user.getId(), mapToDto(saved));
        });

        log.info("📘 New book notification completed for {} users - Book: {}", allUsers.size(), bookTitle);
    }

    @Override
    public void sendNewEventNotification(Long eventId, String eventTitle) {
        // Get all users and send notification
        List<Account> allUsers = accountRepository.findAll();
        log.info("📅 Sending new event notification to {} users for event: {}", allUsers.size(), eventTitle);

        allUsers.forEach(user -> {
            Notification notification = Notification.builder()
                    .title("New Event")
                    .description("A new event '" + eventTitle + "' has been created")
                    .notificationType("NEW_EVENT")
                    .toUser(user)
                    .isRead(false)
                    .createdDate(new Timestamp(System.currentTimeMillis()))
                    .relatedEventId(eventId)
                    .build();

            Notification saved = notificationRepository.save(notification);
            log.info("✅ Notification saved to DB with ID: {} for user: {}", saved.getId(), user.getUsername());
            
            sendWebSocketNotification(user.getId(), mapToDto(saved));
        });

        log.info("📅 New event notification completed for {} users - Event: {}", allUsers.size(), eventTitle);
    }

    @Override
    public void sendReminderNotifications() {
        // Find all borrow records due in 3 days
        LocalDate threeDaysFromNow = LocalDate.now().plusDays(3);
        List<BorrowRecord> dueRecords = borrowRecordRepository.findBorrowRecordsDueInDays(threeDaysFromNow);

        dueRecords.forEach(record -> {
            if (record.getReturnRecord() == null) { // Only for unreturned books
                Account user = record.getLibraryCard().getReader().getAccount();

                Notification notification = Notification.builder()
                        .title("Book Return Reminder")
                        .description("Your book '" + record.getBook().getTitle() + "' is due on " + record.getAllowedDate())
                        .notificationType("REMINDER")
                        .toUser(user)
                        .isRead(false)
                        .createdDate(new Timestamp(System.currentTimeMillis()))
                        .relatedBookId(record.getBook().getId())
                        .relatedBorrowRecordId(record.getId())
                        .build();

                Notification saved = notificationRepository.save(notification);
                sendWebSocketNotification(user.getId(), mapToDto(saved));
            }
        });

        log.info("Reminder notifications sent for {} books due in 3 days", dueRecords.size());
    }

    @Override
    public void sendOverdueNotifications() {
        // Find all overdue borrow records
        LocalDate today = LocalDate.now();
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueBorrowRecords(today);

        overdueRecords.forEach(record -> {
            if (record.getReturnRecord() == null) { // Only for unreturned books
                Account user = record.getLibraryCard().getReader().getAccount();

                Notification notification = Notification.builder()
                        .title("Book Overdue")
                        .description("Your book '" + record.getBook().getTitle() + "' was due on " + record.getAllowedDate() + ". Please return it immediately.")
                        .notificationType("OVERDUE")
                        .toUser(user)
                        .isRead(false)
                        .createdDate(new Timestamp(System.currentTimeMillis()))
                        .relatedBookId(record.getBook().getId())
                        .relatedBorrowRecordId(record.getId())
                        .build();

                Notification saved = notificationRepository.save(notification);
                sendWebSocketNotification(user.getId(), mapToDto(saved));
            }
        });

        log.info("Overdue notifications sent for {} books", overdueRecords.size());
    }

    private void sendWebSocketNotification(Long userId, NotificationDto notificationDto) {
        try {
            log.debug("🔔 Sending WebSocket notification to user {} - Type: {}, Title: {}", 
                    userId, notificationDto.getNotificationType(), notificationDto.getTitle());
            
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    notificationDto
            );
            
            log.debug("✅ WebSocket notification sent successfully to user {}", userId);
        } catch (Exception e) {
            log.error("❌ Failed to send WebSocket notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .description(notification.getDescription())
                .notificationType(notification.getNotificationType())
                .isRead(notification.getIsRead())
                .createdDate(notification.getCreatedDate())
                .readDate(notification.getReadDate())
                .toUserId(notification.getToUser().getId())
                .toUsername(notification.getToUser().getUsername())
                .relatedBookId(notification.getRelatedBookId())
                .relatedEventId(notification.getRelatedEventId())
                .relatedBorrowRecordId(notification.getRelatedBorrowRecordId())
                .build();
    }
}

