package com.library.librarymanagement.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
public class BookRequest {
    private String bookCode;
    private String title;
    private String description;
    private Long authorId;
    private Long publisherId;
    private Long categoryId;
    private MultipartFile image;
    private String publishedDate;
}
