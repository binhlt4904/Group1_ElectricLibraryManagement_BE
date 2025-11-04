package com.library.librarymanagement.dto.response;

import com.library.librarymanagement.entity.Publisher;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter

public class BookResponse {
    private int id;
    private String bookCode;
    private String description;
    private String author;
    private String title;
    private String image;
    private String isDeleted;
    private String category;
    private Date publishedDate;
    private Date importedDate;
    private String publisher;
}
