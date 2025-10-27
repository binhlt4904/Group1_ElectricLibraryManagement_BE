// src/main/java/com/library/librarymanagement/dto/CategoryUpdateRequest.java
package com.library.librarymanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCategoryRequest {

    @NotNull(message = "Category ID must not be null")
    private Long id;

    @NotBlank(message = "Category name must not be blank")
    private String name;
}
