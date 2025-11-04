package com.library.librarymanagement.controller.admin;

import com.library.librarymanagement.dto.request.CreateDocumentRequest;
import com.library.librarymanagement.dto.request.UpdateDocumentRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.DocumentDto;
import com.library.librarymanagement.dto.response.UploadDocumentResponse;
import com.library.librarymanagement.service.document.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/documents")
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload a file and return the file path
     * POST /api/v1/admin/documents/upload
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<UploadDocumentResponse> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {
        log.info("File upload request received: {}", file.getOriginalFilename());
        UploadDocumentResponse response = documentService.uploadFile(file);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Create a new document with metadata
     * POST /api/v1/admin/documents
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> createDocument(
            @Valid @RequestBody CreateDocumentRequest request
    ) {
        log.info("Create document request received: {}", request.getTitle());
        ApiResponse response = documentService.createDocument(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Get all documents with pagination and optional filters
     * GET /api/v1/admin/documents
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Page<DocumentDto>> getAllDocuments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Get all documents request received with filters - title: {}, category: {}", title, categoryName);
        Page<DocumentDto> documents = documentService.getAllDocuments(title, categoryName, page, size);
        return ResponseEntity.ok(documents);
    }

    /**
     * Get a document by ID
     * GET /api/v1/admin/documents/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<DocumentDto> getDocumentById(
            @PathVariable Long id
    ) {
        log.info("Get document request received for ID: {}", id);
        DocumentDto document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    /**
     * Update document metadata
     * PUT /api/v1/admin/documents/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentRequest request
    ) {
        log.info("Update document request received for ID: {}", id);
        ApiResponse response = documentService.updateDocument(id, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Delete a document (soft delete)
     * DELETE /api/v1/admin/documents/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> deleteDocument(
            @PathVariable Long id
    ) {
        log.info("Delete document request received for ID: {}", id);
        ApiResponse response = documentService.deleteDocument(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}

