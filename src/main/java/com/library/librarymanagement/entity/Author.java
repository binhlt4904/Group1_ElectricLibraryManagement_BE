package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "author")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullName", columnDefinition = "nvarchar(MAX)")
    private String fullName;
    private String email;
    @Column(name = "biography", columnDefinition = "nvarchar(MAX)")
    private String biography;

    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Boolean isDeleted;
    private Long createdBy;
    private Long updatedBy;

    @OneToMany(mappedBy = "author")
    private Set<Book> books;
}
