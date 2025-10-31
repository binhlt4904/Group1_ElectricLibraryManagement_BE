package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "library_card")
@Data
public class LibraryCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Cột card_number (unique)
    @Column(name = "card_number", length = 20, nullable = false, unique = true)
    private String cardNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "issue_date")
    private Date issueDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @UpdateTimestamp
    private Timestamp updatedDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", referencedColumnName = "id", nullable = false)
    private Reader reader;


    @OneToMany(mappedBy = "libraryCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CardRenewalDetails> renewalDetails;

    @OneToMany(mappedBy = "libraryCard", fetch = FetchType.LAZY)
    private Set<BorrowRecord> borrowRecords;
}
