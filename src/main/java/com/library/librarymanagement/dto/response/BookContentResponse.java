package com.library.librarymanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookContentResponse {
    private Long id;
    private String chapter;
    private String content;
    private String title;
    private Boolean isDeleted;

}
