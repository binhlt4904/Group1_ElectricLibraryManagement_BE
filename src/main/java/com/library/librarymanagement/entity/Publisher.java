package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "publisher")
@Data
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "companyName", columnDefinition = "nvarchar(MAX)")
    private String companyName;
    private String email;
    private String phone;
    @Column(name = "address", columnDefinition = "nvarchar(MAX)")
    private String address;
    private Integer establishedYear;
    private String website;
    @Column(name = "avatar_url", columnDefinition = "nvarchar(MAX)")
    private String avatarUrl;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Boolean isDeleted;
    private Long createdBy;
    private Long updatedBy;

    @OneToMany(mappedBy = "publisher")
    private Set<Book> books;
}
