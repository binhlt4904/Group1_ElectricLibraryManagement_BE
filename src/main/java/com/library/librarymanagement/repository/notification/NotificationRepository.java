package com.library.librarymanagement.repository.notification;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a specific user
     */
    Page<Notification> findByToUserOrderByCreatedDateDesc(Account toUser, Pageable pageable);

    /**
     * Find unread notifications for a specific user
     */
    @Query("SELECT n FROM Notification n WHERE n.toUser = :toUser AND n.isRead = false ORDER BY n.createdDate DESC")
    List<Notification> findUnreadNotifications(@Param("toUser") Account toUser);

    /**
     * Count unread notifications for a specific user
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.toUser = :toUser AND n.isRead = false")
    Long countUnreadNotifications(@Param("toUser") Account toUser);

    /**
     * Find notifications by type for a specific user
     */
    @Query("SELECT n FROM Notification n WHERE n.toUser = :toUser AND n.notificationType = :type ORDER BY n.createdDate DESC")
    Page<Notification> findByTypeForUser(@Param("toUser") Account toUser, @Param("type") String type, Pageable pageable);

    /**
     * Find notifications created within a date range
     */
    @Query("SELECT n FROM Notification n WHERE n.toUser = :toUser AND n.createdDate BETWEEN :startDate AND :endDate ORDER BY n.createdDate DESC")
    List<Notification> findNotificationsByDateRange(@Param("toUser") Account toUser, @Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    /**
     * Find notifications related to a specific book
     */
    @Query("SELECT n FROM Notification n WHERE n.relatedBookId = :bookId ORDER BY n.createdDate DESC")
    List<Notification> findByRelatedBookId(@Param("bookId") Long bookId);

    /**
     * Find notifications related to a specific event
     */
    @Query("SELECT n FROM Notification n WHERE n.relatedEventId = :eventId ORDER BY n.createdDate DESC")
    List<Notification> findByRelatedEventId(@Param("eventId") Long eventId);

    /**
     * Find notifications related to a specific borrow record
     */
    @Query("SELECT n FROM Notification n WHERE n.relatedBorrowRecordId = :borrowRecordId ORDER BY n.createdDate DESC")
    List<Notification> findByRelatedBorrowRecordId(@Param("borrowRecordId") Long borrowRecordId);

    /**
     * Find all unread notifications of a specific type for a user
     */
    @Query("SELECT n FROM Notification n WHERE n.toUser = :toUser AND n.notificationType = :type AND n.isRead = false ORDER BY n.createdDate DESC")
    List<Notification> findUnreadNotificationsByType(@Param("toUser") Account toUser, @Param("type") String type);
}

