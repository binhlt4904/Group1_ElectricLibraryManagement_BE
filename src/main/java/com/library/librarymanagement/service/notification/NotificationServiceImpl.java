package com.library.librarymanagement.service.notification;

import com.library.librarymanagement.dto.request.SendNotificationRequest;
import com.library.librarymanagement.dto.response.NotificationDto;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Notification;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AccountRepository accountRepository;
    private final NotificationRepository notificationRepository;

    // ================= Query APIs =================
    @Override
    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByAccount_IdOrderByCreatedDateDesc(userId, pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<NotificationDto> getNotificationsByType(Long userId, String type, Pageable pageable) {
        return notificationRepository.findByAccount_IdAndNotificationTypeOrderByCreatedDateDesc(userId, type, pageable)
                .map(this::convertToDto);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByAccount_IdAndIsReadFalse(userId);
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
            log.info("[Notification] Marked notification {} as read", notificationId);
        });
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByAccount_IdAndIsReadFalseOrderByCreatedDateDesc(userId);
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
        log.info("[Notification] Marked all {} notifications as read for user {}", unreadNotifications.size(), userId);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
        log.info("[Notification] Deleted notification {}", notificationId);
    }

    @Override
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findByAccount_IdAndIsReadFalseOrderByCreatedDateDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countByAccount_IdAndIsReadFalse(userId);
    }

    // ================= Helper: Convert Entity to DTO =================
    private NotificationDto convertToDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .description(n.getDescription())
                .message(n.getDescription())
                .notificationType(n.getNotificationType())
                .type(n.getNotificationType())
                .isRead(n.getIsRead())
                .read(n.getIsRead())
                .createdDate(n.getCreatedDate())
                .createdAt(n.getCreatedDate())
                .relatedBookId(n.getRelatedBookId())
                .relatedEventId(n.getRelatedEventId())
                .relatedBorrowRecordId(n.getRelatedBorrowRecordId())
                .relatedCardId(n.getRelatedCardId())
                .build();
    }

    // ================= Sending APIs =================
    @Override
    public void sendNewEventNotification(Long eventId, String eventTitle) {
        log.info("[Notification] Broadcasting NEW_EVENT for event {} - {}", eventId, eventTitle);
        List<Account> targets = accountRepository.findAll();
        for (Account account : targets) {
            Notification notification = Notification.builder()
                    .account(account)
                    .title("New Event: " + eventTitle)
                    .description("A new event has been created.")
                    .notificationType("NEW_EVENT")
                    .relatedEventId(eventId)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            
            NotificationDto dto = convertToDto(notification);
            sendToUsername(account.getUsername(), dto);
        }
    }

    @Override
    public void sendEventUpdateNotification(Long eventId, String eventTitle, String changedFields) {
        log.info("[Notification] Broadcasting EVENT_UPDATED for event {} - {}", eventId, eventTitle);
        List<Account> targets = accountRepository.findAll();
        for (Account account : targets) {
            Notification notification = Notification.builder()
                    .account(account)
                    .title("Event Updated: " + eventTitle)
                    .description("Changes: " + changedFields)
                    .notificationType("EVENT_UPDATED")
                    .relatedEventId(eventId)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            
            NotificationDto dto = convertToDto(notification);
            sendToUsername(account.getUsername(), dto);
        }
    }

    @Override
    public void sendLibraryCardStatusNotification(Long userId, String status) {
        accountRepository.findById(userId).ifPresent(acc -> {
            String statusMessage = status.equals("ACTIVE") ? "Your library card has been reactivated." 
                    : status.equals("SUSPENDED") ? "Your library card has been suspended."
                    : "Your library card status changed to: " + status;
            
            Notification notification = Notification.builder()
                    .account(acc)
                    .title("Library Card " + status)
                    .description(statusMessage)
                    .notificationType("CARD_" + status)
                    .relatedCardId(acc.getId())
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            
            NotificationDto dto = convertToDto(notification);
            sendToUsername(acc.getUsername(), dto);
        });
    }

    @Override
    public void sendCardExpiringNotification(Long userId, int daysUntilExpiry) {
        accountRepository.findById(userId).ifPresent(acc -> {
            Notification notification = Notification.builder()
                    .account(acc)
                    .title("Card Expiring Soon")
                    .description("Your library card will expire in " + daysUntilExpiry + " day(s)")
                    .notificationType("CARD_EXPIRING")
                    .relatedCardId(acc.getId())
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            
            NotificationDto dto = convertToDto(notification);
            sendToUsername(acc.getUsername(), dto);
        });
    }

    @Override
    public void sendBorrowReminderNotification(Long userId, String bookTitle, int daysUntilDue) {
        accountRepository.findById(userId).ifPresent(acc -> {
            Notification notification = Notification.builder()
                    .account(acc)
                    .title("Borrow Reminder")
                    .description("Return '" + bookTitle + "' within " + daysUntilDue + " day(s)")
                    .notificationType("BORROW_REMINDER")
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            
            NotificationDto dto = convertToDto(notification);
            sendToUsername(acc.getUsername(), dto);
        });
    }

    @Override
    public void sendOverdueNotification(Long userId, String bookTitle, int daysOverdue) {
        accountRepository.findById(userId).ifPresent(acc -> {
            Notification notification = Notification.builder()
                    .account(acc)
                    .title("Overdue Notice")
                    .description("'" + bookTitle + "' is overdue by " + daysOverdue + " day(s)")
                    .notificationType("OVERDUE")
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            
            NotificationDto dto = convertToDto(notification);
            sendToUsername(acc.getUsername(), dto);
        });
    }

    @Override
    public void sendNotification(SendNotificationRequest request) {
        if (request.getToUserId() == null) {
            return;
        }
        accountRepository.findById(request.getToUserId()).ifPresent(acc -> {
            Notification notification = Notification.builder()
                    .account(acc)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .notificationType(request.getNotificationType())
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            
            NotificationDto dto = convertToDto(notification);
            sendToUsername(acc.getUsername(), dto);
        });
    }

    // ================= Helpers =================
    private void sendToUsersByUsername(List<Account> accounts, NotificationDto dto) {
        List<String> usernames = accounts.stream()
                .map(Account::getUsername)
                .filter(u -> u != null && !u.isBlank())
                .collect(Collectors.toList());
        for (String username : usernames) {
            sendToUsername(username, dto);
        }
    }

    private void sendToUsername(String username, NotificationDto dto) {
        try {
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/new-notification",
                    dto
            );
            log.info("[WebSocket] Sent notification to /user/{}/queue/new-notification", username);
        } catch (Exception e) {
            log.warn("[WebSocket] Failed to send to {}: {}", username, e.getMessage());
        }
    }
}
