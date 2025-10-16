package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "library_card") // nên dùng snake_case để khớp DB thực tế
public class LibraryCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true, nullable = false) // cột được tham chiếu
    private String cardNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "issue_date")
    private Date issueDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expiry_date")
    private Date expiryDate;

    private String status;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    // ---------------------------
    // Relationships
    // ---------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id")
    private Reader reader;

    @OneToMany(mappedBy = "libraryCard")
    private Set<BorrowRecord> borrowRecords;

    @OneToMany(mappedBy = "libraryCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardRenewalDetails> renewalDetails;
}
