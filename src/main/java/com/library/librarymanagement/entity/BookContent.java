package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "book_content")
public class BookContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer chapter;
    private String title;

    @Lob
    private String content;

    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Boolean isDeleted;
    private Long createdBy;
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    private Book book;


}
