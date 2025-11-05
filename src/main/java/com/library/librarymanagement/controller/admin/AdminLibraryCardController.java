package com.library.librarymanagement.controller.admin;

import com.library.librarymanagement.dto.request.CreateLibraryCardRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.LibraryCardDto;
import com.library.librarymanagement.service.librarycard.LibraryCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/library-cards")
@Slf4j
public class AdminLibraryCardController {
    
    private final LibraryCardService libraryCardService;
    
    /**
     * Create a new library card for a reader
     * POST /api/v1/admin/library-cards
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> createLibraryCard(
            @Valid @RequestBody CreateLibraryCardRequest request
    ) {
        log.info("Admin creating library card for reader ID: {}", request.getReaderId());
        ApiResponse response = libraryCardService.createLibraryCard(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get all library cards (paginated)
     * GET /api/v1/admin/library-cards
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<LibraryCardDto>> getAllLibraryCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Admin fetching all library cards - page: {}, size: {}", page, size);
        List<LibraryCardDto> cards = libraryCardService.getAllLibraryCards(page, size);
        return ResponseEntity.ok(cards);
    }
    
    /**
     * Get library card by reader ID
     * GET /api/v1/admin/library-cards/reader/{readerId}
     */
    @GetMapping("/reader/{readerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<LibraryCardDto> getCardByReaderId(@PathVariable Long readerId) {
        log.info("Admin fetching library card for reader ID: {}", readerId);
        LibraryCardDto card = libraryCardService.getCardByReaderId(readerId);
        return ResponseEntity.ok(card);
    }
    
    /**
     * Update library card status
     * PUT /api/v1/admin/library-cards/{cardId}/status
     */
    @PutMapping("/{cardId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> updateCardStatus(
            @PathVariable Long cardId,
            @RequestParam String status
    ) {
        log.info("Admin updating card {} status to: {}", cardId, status);
        ApiResponse response = libraryCardService.updateCardStatus(cardId, status);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Approve renewal request
     * POST /api/v1/admin/library-cards/renewals/{renewalId}/approve
     */
    @PostMapping("/renewals/{renewalId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> approveRenewal(@PathVariable Long renewalId) {
        log.info("Admin approving renewal request: {}", renewalId);
        ApiResponse response = libraryCardService.approveRenewal(renewalId);
        return ResponseEntity.ok(response);
    }
}
