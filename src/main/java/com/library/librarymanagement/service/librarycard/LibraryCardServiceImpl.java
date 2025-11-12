package com.library.librarymanagement.service.librarycard;

import com.library.librarymanagement.dto.request.CreateLibraryCardRequest;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryCardServiceImpl implements LibraryCardService {
    
    private final LibraryCardRepository libraryCardRepository;
    private final CardRenewalDetailsRepository renewalRepository;
    private final ReaderRepository readerRepository;
    private final com.library.librarymanagement.service.notification.NotificationService notificationService;
    
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
    
    // ==================== ADMIN METHODS ====================
    
    @Override
    @Transactional
    public ApiResponse createLibraryCard(CreateLibraryCardRequest request) {
        try {
            // Check if reader exists
            Reader reader = readerRepository.findById(request.getReaderId())
                    .orElseThrow(() -> new ObjectNotExistException("Reader not found with ID: " + request.getReaderId()));
            
            // Check if reader already has a library card
            if (libraryCardRepository.findByReaderId(request.getReaderId()).isPresent()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Reader already has a library card")
                        .build();
            }
            
            // Generate unique card number
            String cardNumber = generateCardNumber();
            
            // Calculate expiry date
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, request.getValidityYears() != null ? request.getValidityYears() : 1);
            Date expiryDate = calendar.getTime();
            
            // Create library card
            LibraryCard card = new LibraryCard();
            card.setCardNumber(cardNumber);
            card.setIssueDate(new Date());
            card.setExpiryDate(expiryDate);
            card.setStatus("ACTIVE");
            card.setReader(reader);
            card.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            card.setIsDeleted(false);
            
            libraryCardRepository.save(card);
            
            log.info("Library card created successfully: {}", cardNumber);
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Library card created successfully with card number: " + cardNumber)
                    .build();
                    
        } catch (ObjectNotExistException e) {
            log.error("Error creating library card", e);
            return ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Error creating library card", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error creating library card: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LibraryCardDto> getAllLibraryCards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return libraryCardRepository.findAll(pageable)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public LibraryCardDto getCardByReaderId(Long readerId) {
        LibraryCard card = libraryCardRepository.findByReaderId(readerId)
                .orElseThrow(() -> new ObjectNotExistException("Library card not found for reader ID: " + readerId));
        return convertToDto(card);
    }
    
    @Override
    @Transactional
    public ApiResponse updateCardStatus(Long cardId, String status) {
        try {
            LibraryCard card = libraryCardRepository.findById(cardId)
                    .orElseThrow(() -> new ObjectNotExistException("Library card not found with ID: " + cardId));
            
            String oldStatus = card.getStatus();
            card.setStatus(status.toUpperCase());
            card.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            libraryCardRepository.save(card);
            
            log.info("Card {} status updated from {} to: {}", cardId, oldStatus, status);
            
            // Send notification if card is suspended
            if ("SUSPENDED".equalsIgnoreCase(status)) {
                try {
                    Reader reader = card.getReader();
                    if (reader != null && reader.getAccount() != null) {
                        log.info("📧 Sending suspension notification to user: {}", reader.getAccount().getUsername());
                        notificationService.sendNotification(
                            com.library.librarymanagement.dto.request.SendNotificationRequest.builder()
                                .title("Library Card Suspended")
                                .description("Your library card " + card.getCardNumber() + " has been suspended. Please contact the library for more information.")
                                .notificationType("CARD_SUSPENDED")
                                .toUserId(reader.getAccount().getId())
                                .relatedBookId(null)
                                .relatedEventId(null)
                                .relatedBorrowRecordId(null)
                                .build()
                        );
                        log.info("✅ Suspension notification sent to user: {}", reader.getAccount().getUsername());
                    }
                } catch (Exception notifError) {
                    log.error("❌ Failed to send suspension notification: {}", notifError.getMessage());
                    // Don't fail the status update if notification fails
                }
            }
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Card status updated successfully")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error updating card status", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error updating card status: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    @Transactional
    public ApiResponse approveRenewal(Long renewalId) {
        try {
            CardRenewalDetails renewal = renewalRepository.findById(renewalId)
                    .orElseThrow(() -> new ObjectNotExistException("Renewal request not found with ID: " + renewalId));
            
            LibraryCard card = libraryCardRepository.findByCardNumber(renewal.getCardCode())
                    .orElseThrow(() -> new ObjectNotExistException("Library card not found"));
            
            // Update card expiry date
            card.setExpiryDate(renewal.getNewExpiryDate());
            card.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            libraryCardRepository.save(card);
            
            log.info("Renewal approved for card: {}", card.getCardNumber());
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Renewal approved successfully")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error approving renewal", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error approving renewal: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Generate unique card number in format: EL-YYYY-XXXXXX
     */
    private String generateCardNumber() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("EL-%d-%s", year, randomPart);
    }
}
