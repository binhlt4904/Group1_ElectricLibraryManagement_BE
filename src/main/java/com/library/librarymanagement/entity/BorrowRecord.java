package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "BorrowRecord")
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date borrowedDate;

    @Temporal(TemporalType.DATE)
    private Date allowedDate;

    private String status;
    private String accessToken;

    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Long createdBy;
    private Long updatedBy;

    @ManyToOne
    @JoinColumn(name = "library_card_id")
    private LibraryCard libraryCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    private Book book;


    @OneToOne
    @JoinColumn(name = "returned_record_id")
    private ReturnRecord returnRecord;
}
