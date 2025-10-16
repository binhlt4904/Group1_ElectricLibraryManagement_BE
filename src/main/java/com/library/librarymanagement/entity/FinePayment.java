package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "FinePayment")
public class FinePayment {
    @Id
    private Long id; // shared PK with ReturnRecord (returned_id)

    @Temporal(TemporalType.DATE)
    private Date paymentDate;
    private Integer amount;
    private Timestamp createdDate;
    private Long createdBy;

    @OneToOne
    @MapsId
    @JoinColumn(name = "returned_id")
    private ReturnRecord returnRecord;
}
