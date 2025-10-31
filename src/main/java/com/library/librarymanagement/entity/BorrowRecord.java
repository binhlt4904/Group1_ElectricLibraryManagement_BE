package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "borrow_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date borrowedDate;

    @Temporal(TemporalType.DATE)
    private Date allowedDate;

    private String status;
    private String accessToken; // in this token, save with information: libraryCardId,

    @CreationTimestamp
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp updatedDate;
    private Long createdBy; //accountId
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_card_id", referencedColumnName = "id", nullable = false)
    private LibraryCard libraryCard;

    @OneToOne
    @JoinColumn(name = "returned_record_id")
    private ReturnRecord returnRecord;
}
