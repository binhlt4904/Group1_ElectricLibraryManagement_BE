package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "return_record")
@Data
public class ReturnRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date returnedDate;

    private String note;
    private String status;

    @CreationTimestamp
    private Timestamp createdDate;
    private Long createdBy; //accountId

    @OneToOne(mappedBy = "returnRecord")
    private BorrowRecord borrowRecord;


}
