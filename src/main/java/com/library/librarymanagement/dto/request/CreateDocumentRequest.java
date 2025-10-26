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
public class CreateDocumentRequest {

    @NotBlank(message = "Document title cannot be blank")
    private String title;

    @NotBlank(message = "Category name cannot be blank")
    private String categoryName;

    @NotBlank(message = "File path cannot be blank")
    private String filePath;
}

