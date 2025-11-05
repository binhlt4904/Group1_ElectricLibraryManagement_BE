package com.library.librarymanagement.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class BookUpdateRequest {
    private String title;
    private String author;
    private String publisher;
    private Date publishedDate;
    private String category;
    private Boolean isDeleted;
}
