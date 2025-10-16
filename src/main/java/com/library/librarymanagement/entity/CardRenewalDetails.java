package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "card_renewal_details")
public class CardRenewalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Cột card_code là FK tới library_card.card_number
    @Column(name = "card_code", length = 20, nullable = false)
    private String cardCode;

    @Temporal(TemporalType.DATE)
    @Column(name = "new_expiry_date")
    private Date newExpiryDate;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "created_date")
    private Timestamp createdDate;

    // ✅ Nhiều chi tiết gia hạn thuộc về 1 thẻ thư viện
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_code", referencedColumnName = "card_number", insertable = false, updatable = false)
    private LibraryCard libraryCard;

    // getters/setters ...
}
