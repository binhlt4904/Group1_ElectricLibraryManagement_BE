package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(
        name = "book",
        uniqueConstraints = @UniqueConstraint(name = "UQ_book_code", columnNames = "book_code")
)
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                       // PK cho tất cả quan hệ

    @Column(name = "book_code", nullable = false, length = 100)
    private String bookCode;               // SKU / mã sản phẩm (unique ở constraint trên)

    @Column(name = "title", length = 255)
    private String title;

    // @Lob
    // Now comment annotation @Lob in this entity to avoid error
    @Column(name = "description")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "imported_date")
    private Date importedDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "published_date")
    private Date publishedDate;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", referencedColumnName = "id")
    private Publisher publisher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_name", referencedColumnName = "name")
    private Category category;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<BookContent> contents;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<BorrowRecord> borrowRecords;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<Review> reviews;

}
