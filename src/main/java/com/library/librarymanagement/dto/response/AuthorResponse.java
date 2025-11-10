package com.library.librarymanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
public class AuthorResponse {
    private Long id;
    private String fullName;
    private String email;
    private String biography;
    private String avatarUrl;
    private String gender;
    private Date birthDate;
    private Date deathDate;
    private String nationality;
    private String website;
    private String socialLinks;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Boolean isDeleted;
}
