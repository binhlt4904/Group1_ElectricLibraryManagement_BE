package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reviewerId;
    private String note;
    private Integer rate;
    private Timestamp createdDate;

    @ManyToOne
    @JoinColumn(name = "book_code", referencedColumnName = "bookCode")
    private Book book;
}
