package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "Reader")
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String readerCode;

    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Boolean isDeleted;
    private Long createdBy;
    private Long updatedBy;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "reader")
    private Set<Report> reports;

    @OneToMany(mappedBy = "reader")
    private Set<LibraryCard> libraryCards;
}
