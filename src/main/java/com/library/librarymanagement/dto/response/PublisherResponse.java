package com.library.librarymanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class PublisherResponse {
    private Long id;
    private String companyName;
    private String email;
    private String phone;
    private String address;
    private Integer establishedYear;
    private String website;
    private String avatarUrl;
    private String description;
    private Boolean isDeleted;
    private Timestamp createdDate;
    private Timestamp updatedDate;
}
