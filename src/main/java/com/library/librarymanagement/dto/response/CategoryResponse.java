package com.library.librarymanagement.dto.response;// src/main/java/com/library/librarymanagement/dto/CategoryResponse.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private Date createdDate;
    private Date updatedDate;
    private Boolean isDeleted;
    private Long createdBy;
    private Long updatedBy;
}
