package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "library_card")
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
    private Timestamp createdDate;

    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    // ✅ Một thẻ thư viện thuộc về một người đọc
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", referencedColumnName = "id", nullable = false)
    private Reader reader;

    // ✅ Một thẻ thư viện có thể có nhiều chi tiết gia hạn
    @OneToMany(mappedBy = "libraryCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CardRenewalDetails> renewalDetails;

    // getters/setters ...
}
