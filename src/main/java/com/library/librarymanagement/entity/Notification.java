package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "notification_type", length = 50)
    private String notificationType; // NEW_BOOK, NEW_EVENT, REMINDER, OVERDUE

    @Column(name = "is_read")
    private Boolean isRead;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Timestamp createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "read_date")
    private Timestamp readDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user", referencedColumnName = "id", nullable = false)
    private Account toUser;

    @Column(name = "related_book_id")
    private Long relatedBookId; // For NEW_BOOK and REMINDER notifications

    @Column(name = "related_event_id")
    private Long relatedEventId; // For NEW_EVENT notifications

    @Column(name = "related_borrow_record_id")
    private Long relatedBorrowRecordId; // For REMINDER and OVERDUE notifications
}
