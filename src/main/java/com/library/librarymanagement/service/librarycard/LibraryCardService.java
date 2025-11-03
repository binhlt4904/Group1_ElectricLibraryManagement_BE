package com.library.librarymanagement.service.librarycard;

import com.library.librarymanagement.dto.request.RenewalRequestDto;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.LibraryCardDto;

public interface LibraryCardService {
    
    /**
     * Get the current user's library card
     */
    LibraryCardDto getMyLibraryCard();
    
    /**
     * Request library card renewal
     */
    ApiResponse requestRenewal(RenewalRequestDto request);
}
