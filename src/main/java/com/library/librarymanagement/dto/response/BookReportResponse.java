package com.library.librarymanagement.dto.response;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReportResponse {
    private Long id;
    private String bookTitle;
    private String authorName;
    private String reporterName;
    private String reporterEmail;
    private String description;
    private String reportType;
    private String staffName;
    private String status;
    private Date createdAt;

}
