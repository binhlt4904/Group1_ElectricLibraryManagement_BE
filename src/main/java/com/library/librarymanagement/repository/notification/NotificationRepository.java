package com.library.librarymanagement.repository.notification;

import com.library.librarymanagement.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Get all notifications for a user, ordered by created date descending
     */
    Page<Notification> findByAccount_IdOrderByCreatedDateDesc(Long accountId, Pageable pageable);

    /**
     * Get unread notifications for a user
     */
    List<Notification> findByAccount_IdAndIsReadFalseOrderByCreatedDateDesc(Long accountId);

    /**
     * Get unread notifications for a user with pagination
     */
    Page<Notification> findByAccount_IdAndIsReadFalseOrderByCreatedDateDesc(Long accountId, Pageable pageable);

    /**
     * Count unread notifications for a user
     */
    long countByAccount_IdAndIsReadFalse(Long accountId);

    /**
     * Get notifications by type for a user
     */
    Page<Notification> findByAccount_IdAndNotificationTypeOrderByCreatedDateDesc(Long accountId, String notificationType, Pageable pageable);

    /**
     * Get notifications by type for a user (unread only)
     */
    Page<Notification> findByAccount_IdAndNotificationTypeAndIsReadFalseOrderByCreatedDateDesc(Long accountId, String notificationType, Pageable pageable);

    /**
     * Delete all notifications for a user
     */
    void deleteByAccount_Id(Long accountId);

    /**
     * Find notifications by related event ID
     */
    List<Notification> findByRelatedEventId(Long eventId);

    /**
     * Find notifications by related book ID
     */
    List<Notification> findByRelatedBookId(Long bookId);

    /**
     * Find notifications by related card ID
     */
    List<Notification> findByRelatedCardId(Long cardId);
}
