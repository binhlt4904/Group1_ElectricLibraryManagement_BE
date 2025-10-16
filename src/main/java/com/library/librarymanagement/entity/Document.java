package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nhiều document thuộc 1 category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_name", referencedColumnName = "name")
    private Category category;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "file_path")
    private String filePath;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "imported_date")
    private Date importedDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "title")
    private String title;


}
