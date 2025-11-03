package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "return_record")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date returnedDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal fineAmount;

    private String note;
    private String status;

    private Long createdBy; //accountId

    @OneToOne(mappedBy = "returnRecord")
    private BorrowRecord borrowRecord;


}
