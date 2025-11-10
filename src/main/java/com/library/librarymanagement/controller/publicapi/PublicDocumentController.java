package com.library.librarymanagement.controller.publicapi;

import com.library.librarymanagement.dto.response.DocumentDto;
import com.library.librarymanagement.service.document.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public Document Controller
 * Provides endpoints for accessing public documents without authentication
 * All endpoints are accessible to unauthenticated users
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/documents")
@Slf4j
public class PublicDocumentController {

    private final DocumentService documentService;

    /**
     * Get all public documents with pagination and optional filters
     * GET /api/v1/public/documents
     * 
     * @param title Optional title search filter
     * @param categoryName Optional category filter
     * @param page Page number (0-indexed, default: 0)
     * @param size Page size (default: 10)
     * @return Paginated list of public documents
     */
    @GetMapping
    public ResponseEntity<Page<DocumentDto>> getPublicDocuments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Get public documents request received with filters - title: {}, category: {}", title, categoryName);
        Page<DocumentDto> documents = documentService.getPublicDocuments(title, categoryName, page, size);
        return ResponseEntity.ok(documents);
    }
}

