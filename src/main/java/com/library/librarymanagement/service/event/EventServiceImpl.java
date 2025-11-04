package com.library.librarymanagement.service.event;

import com.library.librarymanagement.dto.request.CreateEventRequest;
import com.library.librarymanagement.dto.request.UpdateEventRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.EventDto;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Event;
import com.library.librarymanagement.entity.SystemUser;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.EventRepository;
import com.library.librarymanagement.repository.SystemUserRepository;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    
    private final String uploadDir = "uploads/events";
    
    private final EventRepository eventRepository;
    private final SystemUserRepository systemUserRepository;
    private final AccountRepository accountRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<EventDto> getAllEvents(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Event> events = eventRepository.searchEvents(title, pageable);
        return events.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ObjectNotExistException("Event not found with ID: " + id));
        return convertToDto(event);
    }
    
    @Override
    @Transactional
    public ApiResponse createEvent(CreateEventRequest request) {
        try {
            Long userId = getCurrentUserId();

            if (userId == null) {
                return ApiResponse.builder()
                        .success(false)
                        .message("User not authenticated")
                        .build();
            }

            // First, verify that the Account exists
            Account account = accountRepository.findById(userId).orElse(null);
            if (account == null) {
                log.error("Account not found with ID: {}", userId);
                return ApiResponse.builder()
                        .success(false)
                        .message("Account not found. Please contact administrator.")
                        .build();
            }

            // Get SystemUser for the current account
            // If SystemUser doesn't exist, create one automatically for admin/librarian
            SystemUser systemUser = systemUserRepository.findByAccountId(userId).orElse(null);

            if (systemUser == null) {
                log.info("SystemUser not found for account ID: {}. Creating one...", userId);
                SystemUser newSystemUser = new SystemUser();
                newSystemUser.setAccount(account);
                newSystemUser.setJoinDate(new java.sql.Timestamp(System.currentTimeMillis()));
                newSystemUser.setCreatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
                newSystemUser.setIsDeleted(false);
                systemUser = systemUserRepository.save(newSystemUser);
                log.info("SystemUser created successfully with ID: {}", systemUser.getId());
            }
            
            // Handle image upload
            String imageUrl = null;
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                try {
                    // Create upload directory if it doesn't exist
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    
                    // Generate unique filename to avoid collisions
                    String fileName = UUID.randomUUID() + "_" + request.getImage().getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    
                    // Save the file
                    Files.copy(request.getImage().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    
                    // Store the URL path for frontend display
                    imageUrl = "/uploads/events/" + fileName;
                    log.info("Event image uploaded successfully: {}", imageUrl);
                } catch (IOException e) {
                    log.error("Error uploading event image", e);
                    return ApiResponse.builder()
                            .success(false)
                            .message("Error uploading image: " + e.getMessage())
                            .build();
                }
            }
            
            Event event = new Event();
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setEventDate(request.getEventDate());
            event.setStartTime(request.getStartTime());
            event.setEndTime(request.getEndTime());
            event.setLocation(request.getLocation());
            event.setCategory(request.getCategory());
            event.setCapacity(request.getCapacity());
            event.setRegistered(0); // Initialize registered count to 0
            event.setStatus(request.getStatus() != null ? request.getStatus() : "upcoming");
            event.setImageUrl(imageUrl); // Set the uploaded image URL
            event.setCreatedDate(new Date());
            event.setFromUser(systemUser);
            event.setIsDeleted(false);

            eventRepository.save(event);
            
            log.info("Event created successfully with ID: {}", event.getId());
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Event created successfully")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error creating event", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error creating event: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    @Transactional
    public ApiResponse updateEvent(Long id, UpdateEventRequest request) {
        try {
            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new ObjectNotExistException("Event not found with ID: " + id));

            // Update title if provided
            if (request.getTitle() != null && !request.getTitle().isBlank()) {
                event.setTitle(request.getTitle());
            }

            // Update description if provided
            if (request.getDescription() != null && !request.getDescription().isBlank()) {
                event.setDescription(request.getDescription());
            }

            // Update event date if provided
            if (request.getEventDate() != null) {
                event.setEventDate(request.getEventDate());
            }

            // Update start time if provided
            if (request.getStartTime() != null) {
                event.setStartTime(request.getStartTime());
            }

            // Update end time if provided
            if (request.getEndTime() != null) {
                event.setEndTime(request.getEndTime());
            }

            // Update location if provided
            if (request.getLocation() != null) {
                event.setLocation(request.getLocation());
            }

            // Update category if provided
            if (request.getCategory() != null) {
                event.setCategory(request.getCategory());
            }

            // Update capacity if provided
            if (request.getCapacity() != null) {
                event.setCapacity(request.getCapacity());
            }

            // Update status if provided
            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                event.setStatus(request.getStatus());
            }

            // Handle image upload if new image is provided
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                try {
                    // Create upload directory if it doesn't exist
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // Generate unique filename
                    String fileName = UUID.randomUUID() + "_" + request.getImage().getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);

                    // Save the new file
                    Files.copy(request.getImage().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Update image URL
                    String imageUrl = "/uploads/events/" + fileName;
                    event.setImageUrl(imageUrl);
                    log.info("Event image updated successfully: {}", imageUrl);
                } catch (IOException e) {
                    log.error("Error uploading event image during update", e);
                    return ApiResponse.builder()
                            .success(false)
                            .message("Error uploading image: " + e.getMessage())
                            .build();
                }
            }

            // Update the updated date
            event.setUpdatedDate(new Date());

            eventRepository.save(event);

            log.info("Event updated successfully with ID: {}", id);

            return ApiResponse.builder()
                    .success(true)
                    .message("Event updated successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error updating event", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error updating event: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    @Transactional
    public ApiResponse deleteEvent(Long id) {
        try {
            Event event = eventRepository.findById(id)
                    .orElseThrow(() -> new ObjectNotExistException("Event not found with ID: " + id));
            
            eventRepository.delete(event);
            
            log.info("Event deleted successfully with ID: {}", id);
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Event deleted successfully")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error deleting event", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error deleting event: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * Calculate automatic status based on event date and time
     * - "upcoming": current date/time is before event start
     * - "ongoing": current date/time is between event start and end
     * - "completed": current date/time is after event end
     * - "cancelled": only set manually (no automatic transition)
     */
    private String calculateEventStatus(Event event) {
        // If status is manually set to cancelled, keep it
        if ("cancelled".equalsIgnoreCase(event.getStatus())) {
            return "cancelled";
        }

        // If event doesn't have date/time information, return current status or default
        if (event.getEventDate() == null) {
            return event.getStatus() != null ? event.getStatus() : "upcoming";
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventStart = event.getEventDate().atTime(
            event.getStartTime() != null ? event.getStartTime() : java.time.LocalTime.MIN
        );
        LocalDateTime eventEnd = event.getEventDate().atTime(
            event.getEndTime() != null ? event.getEndTime() : java.time.LocalTime.MAX
        );

        // Determine status based on current time
        if (now.isBefore(eventStart)) {
            return "upcoming";
        } else if (now.isAfter(eventEnd)) {
            return "completed";
        } else {
            return "ongoing";
        }
    }

    private EventDto convertToDto(Event event) {
        SystemUser fromUser = event.getFromUser();
        Account account = fromUser != null ? fromUser.getAccount() : null;

        // Calculate automatic status
        String calculatedStatus = calculateEventStatus(event);

        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .category(event.getCategory())
                .capacity(event.getCapacity())
                .registered(event.getRegistered())
                .status(calculatedStatus)  // Use calculated status instead of stored status
                .imageUrl(event.getImageUrl())
                .createdDate(event.getCreatedDate())
                .updatedDate(event.getUpdatedDate())
                .createdBy(fromUser != null ? fromUser.getId() : null)
                .createdByName(account != null ? account.getUsername() : "Unknown")
                .organizerFullName(account != null ? account.getFullName() : "Unknown Organizer")
                .organizerEmail(account != null ? account.getEmail() : null)
                .build();
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found");
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getAccountId();
        }
        
        return null;
    }
}
