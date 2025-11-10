package com.library.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "publisher")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    private Long createdBy;
    private Long updatedBy;
    @Column(name = "description", columnDefinition = "nvarchar(MAX)")
    private String description;
    @OneToMany(mappedBy = "publisher", fetch = FetchType.LAZY)
    private Set<Book> books;
}
