package com.library.librarymanagement.service.librarycard;

import com.library.librarymanagement.dto.request.CreateLibraryCardRequest;
import com.library.librarymanagement.dto.request.RenewalRequestDto;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.LibraryCardDto;

import java.util.List;

public interface LibraryCardService {
    
    /**
     * Get the current user's library card
     */
    LibraryCardDto getMyLibraryCard();
    
    /**
     * Request library card renewal
     */
    ApiResponse requestRenewal(RenewalRequestDto request);
    
    /**
     * Create a new library card for a reader (Admin only)
     */
    ApiResponse createLibraryCard(CreateLibraryCardRequest request);
    
    /**
     * Get all library cards (Admin only)
     */
    List<LibraryCardDto> getAllLibraryCards(int page, int size);
    
    /**
     * Get library card by reader ID (Admin only)
     */
    LibraryCardDto getCardByReaderId(Long readerId);
    
    /**
     * Update library card status (Admin only)
     */
    ApiResponse updateCardStatus(Long cardId, String status);
    
    /**
     * Approve renewal request (Admin only)
     */
    ApiResponse approveRenewal(Long renewalId);
}
