package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "Publisher")
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String email;
    private String phone;
    private String address;
    private Integer establishedYear;
    private String website;

    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Boolean isDeleted;
    private Long createdBy;
    private Long updatedBy;

    @OneToMany(mappedBy = "publisher")
    private Set<Book> books;
}
