// src/main/java/com/library/librarymanagement/dto/CategoryCreateRequest.java
package com.library.librarymanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCatgoryRequest {
    @NotBlank(message = "Category name must not be blank")
    private String name;
}
