package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "author")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", columnDefinition = "nvarchar(255)")
    private String fullName;

    @Column(name = "email", columnDefinition = "varchar(255)")
    private String email;

    @Column(name = "biography", columnDefinition = "nvarchar(MAX)")
    private String biography;

    @Column(name = "avatar_url", columnDefinition = "nvarchar(MAX)")
    private String avatarUrl;

    // 🔹 Bổ sung thông tin cá nhân
    @Column(name = "gender", length = 10)
    private String gender; // "Male", "Female", "Other"

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "death_date")
    private Date deathDate;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "website", columnDefinition = "nvarchar(255)")
    private String website;

    @Column(name = "social_links", columnDefinition = "nvarchar(MAX)")
    private String socialLinks; // có thể lưu JSON chứa link FB, Twitter, v.v.

    // 🔹 Metadata hệ thống
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Boolean isDeleted;
    private Long createdBy;
    private Long updatedBy;

    // 🔹 Quan hệ
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<Book> books;
}
