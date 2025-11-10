package com.library.librarymanagement.dto.request;

import lombok.Data;

@Data
public class PublisherRequest {
    private String companyName;
    private String email;
    private String phone;
    private String address;
    private Integer establishedYear;
    private String website;
    private String avatarUrl;
    private String description;
    private Boolean isDeleted;

}
