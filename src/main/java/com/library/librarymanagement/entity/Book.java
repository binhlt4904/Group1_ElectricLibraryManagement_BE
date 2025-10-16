package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Book")
public class Book {
    @Id
    private String bookCode;

    private String title;
    private String description;
    @Temporal(TemporalType.DATE)
    private Date importedDate;
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToOne
    @JoinColumn(name = "Categoryname")
    private Category category;

    @OneToMany(mappedBy = "book")
    private Set<BookContent> contents;

    @OneToMany(mappedBy = "book")
    private Set<Report> reports;

    @OneToMany(mappedBy = "book")
    private Set<Review> reviews;
}
