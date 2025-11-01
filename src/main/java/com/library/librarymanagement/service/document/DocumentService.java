package com.library.librarymanagement.service.document;

import com.library.librarymanagement.dto.request.CreateDocumentRequest;
import com.library.librarymanagement.dto.request.UpdateDocumentRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.DocumentDto;
import com.library.librarymanagement.dto.response.UploadDocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    /**
     * Upload a file and return the file path
     */
    UploadDocumentResponse uploadFile(MultipartFile file);

    /**
     * Create a new document with metadata
     */
    ApiResponse createDocument(CreateDocumentRequest request);

    /**
     * Get all documents with pagination and optional filters
     */
    Page<DocumentDto> getAllDocuments(String title, String categoryName, int page, int size);

    /**
     * Get a document by ID
     */
    DocumentDto getDocumentById(Long id);

    /**
     * Update document metadata
     */
    ApiResponse updateDocument(Long id, UpdateDocumentRequest request);

    /**
     * Delete a document (soft delete)
     */
    ApiResponse deleteDocument(Long id);
}

