package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Account account;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "notification_type", length = 50, nullable = false)
    private String notificationType; // NEW_BOOK, NEW_EVENT, EVENT_UPDATED, CARD_ACTIVE, CARD_SUSPENDED, CARD_EXPIRING, BORROW_REMINDER, OVERDUE

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "updated_date")
    private Instant updatedDate;

    // Navigation fields to link to related entities
    @Column(name = "related_book_id")
    private Long relatedBookId;

    @Column(name = "related_event_id")
    private Long relatedEventId;

    @Column(name = "related_borrow_record_id")
    private Long relatedBorrowRecordId;

    @Column(name = "related_card_id")
    private Long relatedCardId;

    @PrePersist
    protected void onCreate() {
        createdDate = Instant.now();
        updatedDate = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = Instant.now();
    }
}
