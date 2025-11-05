package com.library.librarymanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLibraryCardRequest {
    
    @NotNull(message = "Reader ID is required")
    private Long readerId;
    
    private Integer validityYears = 1; // Default 1 year validity
}
