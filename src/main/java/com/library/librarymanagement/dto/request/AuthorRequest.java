package com.library.librarymanagement.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class AuthorRequest {
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
}
