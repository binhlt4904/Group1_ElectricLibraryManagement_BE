package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "ReturnRecord")
public class ReturnRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date returnedDate;

    private String note;
    private String status;

    private Timestamp createdDate;
    private Long createdBy;

    @OneToOne(mappedBy = "returnRecord")
    private BorrowRecord borrowRecord;

    @OneToOne(mappedBy = "returnRecord", cascade = CascadeType.ALL)
    private FinePayment finePayment;
}
