package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "CardRenewalDetails")
public class CardRenewalDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date newExpiryDate;

    private String reason;
    private Timestamp createdDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_code", referencedColumnName = "card_number") //
    private LibraryCard libraryCard;
}
