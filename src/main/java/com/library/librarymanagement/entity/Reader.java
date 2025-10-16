package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "reader")
public class Reader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reader_code", nullable = false, unique = true)
    private String readerCode;

    @Column(name = "created_date") private java.sql.Timestamp createdDate;
    @Column(name = "updated_date") private java.sql.Timestamp updatedDate;
    @Column(name = "is_deleted")   private Boolean isDeleted;
    @Column(name = "created_by")   private Long createdBy;
    @Column(name = "updated_by")   private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    // ✅ 1 reader -> N report (khớp mappedBy = "createdBy" ở Report)
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private java.util.Set<Report> reports;

    // 1–1 với LibraryCard (FK ở library_card.reader_id UNIQUE)
    @OneToOne(mappedBy = "reader", fetch = FetchType.LAZY, optional = false)
    private LibraryCard libraryCard;
}
