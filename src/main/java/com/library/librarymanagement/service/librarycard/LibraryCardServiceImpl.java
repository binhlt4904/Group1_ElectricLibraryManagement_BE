package com.library.librarymanagement.service.librarycard;

import com.library.librarymanagement.dto.request.RenewalRequestDto;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.LibraryCardDto;
import com.library.librarymanagement.entity.CardRenewalDetails;
import com.library.librarymanagement.entity.LibraryCard;
import com.library.librarymanagement.entity.Reader;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.CardRenewalDetailsRepository;
import com.library.librarymanagement.repository.LibraryCardRepository;
import com.library.librarymanagement.repository.ReaderRepository;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryCardServiceImpl implements LibraryCardService {
    
    private final LibraryCardRepository libraryCardRepository;
    private final CardRenewalDetailsRepository renewalRepository;
    private final ReaderRepository readerRepository;
    
    @Override
    @Transactional(readOnly = true)
    public LibraryCardDto getMyLibraryCard() {
        Long readerId = getCurrentReaderId();
        
        if (readerId == null) {
            throw new ObjectNotExistException("Reader not found for current user");
        }
        
        LibraryCard card = libraryCardRepository.findByReaderId(readerId)
                .orElseThrow(() -> new ObjectNotExistException("Library card not found for reader ID: " + readerId));
        
        return convertToDto(card);
    }
    
    @Override
    @Transactional
    public ApiResponse requestRenewal(RenewalRequestDto request) {
        try {
            Long readerId = getCurrentReaderId();
            
            if (readerId == null) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Reader not found for current user")
                        .build();
            }
            
            LibraryCard card = libraryCardRepository.findByReaderId(readerId)
                    .orElseThrow(() -> new ObjectNotExistException("Library card not found"));
            
            // Check if there's already a recent renewal request (within last 30 days)
            List<CardRenewalDetails> recentRenewals = renewalRepository.findRecentRenewalsByCardCode(card.getCardNumber());
            if (!recentRenewals.isEmpty()) {
                CardRenewalDetails latestRenewal = recentRenewals.get(0);
                long daysSinceLastRequest = TimeUnit.MILLISECONDS.toDays(
                    System.currentTimeMillis() - latestRenewal.getCreatedDate().getTime()
                );
                
                if (daysSinceLastRequest < 30) {
                    return ApiResponse.builder()
                            .success(false)
                            .message("You already have a recent renewal request. Please wait before submitting another.")
                            .build();
                }
            }
            
            // Create renewal request
            CardRenewalDetails renewal = new CardRenewalDetails();
            renewal.setCardCode(card.getCardNumber());
            renewal.setReason(request.getReason());
            renewal.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            
            // Calculate new expiry date (1 year from current expiry or today, whichever is later)
            Date baseDate = card.getExpiryDate() != null && card.getExpiryDate().after(new Date()) 
                ? card.getExpiryDate() 
                : new Date();
            
            long oneYearInMillis = 365L * 24 * 60 * 60 * 1000;
            Date newExpiryDate = new Date(baseDate.getTime() + oneYearInMillis);
            renewal.setNewExpiryDate(newExpiryDate);
            
            renewalRepository.save(renewal);
            
            log.info("Renewal request created for card: {}", card.getCardNumber());
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Renewal request submitted successfully")
                    .build();
                    
        } catch (ObjectNotExistException e) {
            log.error("Error creating renewal request", e);
            return ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Error submitting renewal request", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error submitting renewal request: " + e.getMessage())
                    .build();
        }
    }
    
    private LibraryCardDto convertToDto(LibraryCard card) {
        Reader reader = card.getReader();
        
        // Calculate if card is expired
        Date now = new Date();
        boolean isExpired = card.getExpiryDate() != null && card.getExpiryDate().before(now);
        
        // Calculate days until expiry
        Integer daysUntilExpiry = null;
        if (card.getExpiryDate() != null && !isExpired) {
            long diffInMillies = card.getExpiryDate().getTime() - now.getTime();
            daysUntilExpiry = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        
        return LibraryCardDto.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .issueDate(card.getIssueDate())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus())
                .readerId(reader != null ? reader.getId() : null)
                .readerName(reader != null && reader.getAccount() != null ? reader.getAccount().getFullName() : null)
                .readerCode(reader != null ? reader.getReaderCode() : null)
                .isExpired(isExpired)
                .daysUntilExpiry(daysUntilExpiry)
                .build();
    }
    
    private Long getCurrentReaderId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found");
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getReaderId();
        }
        
        log.warn("Principal is not an instance of CustomUserDetails");
        return null;
    }
}
