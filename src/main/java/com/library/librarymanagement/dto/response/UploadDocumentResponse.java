package com.library.librarymanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadDocumentResponse {

    private boolean success;

    private String message;

    private String filePath;

    private String fileName;

    private Long fileSize;
}

