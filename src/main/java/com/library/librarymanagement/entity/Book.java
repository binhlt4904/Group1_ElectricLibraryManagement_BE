package com.library.librarymanagement.entity;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "book",
        uniqueConstraints = @UniqueConstraint(name = "UQ_book_code", columnNames = "book_code")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                       // PK cho tất cả quan hệ

    @Column(name = "book_code", nullable = false, length = 100)
    private String bookCode;               // SKU / mã sản phẩm (unique ở constraint trên)

    @Column(name = "title", length = 255)
    private String title;

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
