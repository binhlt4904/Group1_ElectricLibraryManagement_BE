package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "SystemUser")
public class SystemUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Timestamp joinDate;
    private String position;
    private Timestamp hireDate;
    private BigDecimal salary;

    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Boolean isDeleted;
    private Long createdBy;
    private Long updatedBy;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
