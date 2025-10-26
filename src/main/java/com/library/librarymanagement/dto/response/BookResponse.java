package com.library.librarymanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class BookResponse {
    private int id;
    private String author;
    private String title;
    private String image;
    private String isDeleted;
    private String category;
    private Date publishedDate;
}
