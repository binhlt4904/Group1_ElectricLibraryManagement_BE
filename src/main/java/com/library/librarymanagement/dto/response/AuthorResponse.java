package com.library.librarymanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorResponse {
    private Long id;
    private String fullName;
    private String email;
    private String biography;
}
