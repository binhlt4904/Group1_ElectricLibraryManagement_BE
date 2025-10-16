package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String note;
    private String status;
    private Long confirmedBy;

    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Boolean isDeleted;
    private Long createdBy;
    private Long updatedBy;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
