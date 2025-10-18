package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "fine_payment")
@Data
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
