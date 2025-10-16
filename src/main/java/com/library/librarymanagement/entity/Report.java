package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    private Book book;


    // ✅ N report -> 1 reader (created_by -> reader.id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", referencedColumnName = "id", nullable = false)
    private Reader createdBy;

    @Column(name = "created_date")
    private java.sql.Timestamp createdDate;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "status", length = 255)
    private String status;

    // confirmed_by vẫn là Account (nếu ERD như hình)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by", referencedColumnName = "id")
    private Account confirmedBy;

}