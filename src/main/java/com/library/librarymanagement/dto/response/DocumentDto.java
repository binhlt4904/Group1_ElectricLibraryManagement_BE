package com.library.librarymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDto {

    private Long id;

    private String title;

    private String description;

    private String categoryName;

    private String filePath;

    private String fileName;

    private String accessLevel;

    private Date importedDate;

    private Long createdBy;

    private Boolean isDeleted;
}

