package com.library.librarymanagement.controller;

import com.library.librarymanagement.dto.request.RenewalRequestDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/library-card")
@Slf4j
public class LibraryCardController {
    
    private final LibraryCardService libraryCardService;
    
    /**
     * Get current user's library card
     * GET /api/v1/users/me/library-card
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LibraryCardDto> getMyLibraryCard() {
        log.info("Get library card request for current user");
        LibraryCardDto card = libraryCardService.getMyLibraryCard();
        return ResponseEntity.ok(card);
    }
    
    /**
     * Request library card renewal
     * POST /api/v1/users/me/library-card/renew
     */
    @PostMapping("/renew")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> requestRenewal(
            @Valid @RequestBody RenewalRequestDto request
    ) {
        log.info("Library card renewal request from current user");
        ApiResponse response = libraryCardService.requestRenewal(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
