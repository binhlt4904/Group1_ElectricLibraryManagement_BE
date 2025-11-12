package com.library.librarymanagement.controller.user;

import com.library.librarymanagement.dto.request.EventRegistrationRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.EventRegistrationDto;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import com.library.librarymanagement.service.event.EventRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Slf4j
public class EventRegistrationController {
    
    private final EventRegistrationService eventRegistrationService;
    
    /**
     * Register for an event
     * POST /api/v1/events/{eventId}/register
     */
    @PostMapping("/{eventId}/register")
    @PreAuthorize("hasAnyRole('READER', 'USER', 'ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> registerForEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventRegistrationRequest request
    ) {
        log.info("Register for event request - Event ID: {}", eventId);
        ApiResponse response = eventRegistrationService.registerForEvent(eventId, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Cancel registration for an event
     * DELETE /api/v1/events/{eventId}/register
     */
    @DeleteMapping("/{eventId}/register")
    @PreAuthorize("hasAnyRole('READER', 'USER', 'ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> cancelRegistration(@PathVariable Long eventId) {
        log.info("Cancel registration request - Event ID: {}", eventId);
        ApiResponse response = eventRegistrationService.cancelRegistration(eventId);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get all registrations for an event (admin only)
     * GET /api/v1/events/{eventId}/registrations
     */
    @GetMapping("/{eventId}/registrations")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Page<EventRegistrationDto>> getEventRegistrations(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Get event registrations - Event ID: {}, page: {}, size: {}", eventId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<EventRegistrationDto> registrations = eventRegistrationService.getEventRegistrations(eventId, pageable);
        return ResponseEntity.ok(registrations);
    }
    
    /**
     * Check if user is registered for an event
     * GET /api/v1/events/{eventId}/is-registered
     */
    @GetMapping("/{eventId}/is-registered")
    @PreAuthorize("hasAnyRole('READER', 'USER', 'ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> isUserRegistered(@PathVariable Long eventId) {
        log.info("Check registration status - Event ID: {}", eventId);
        boolean isRegistered = eventRegistrationService.isUserRegistered(eventId, getCurrentUserId());
        
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Registration status retrieved")
                .data(java.util.Map.of("isRegistered", isRegistered))
                .build());
    }
    
    /**
     * Get registration details
     * GET /api/v1/events/{eventId}/registration
     */
    @GetMapping("/{eventId}/registration")
    @PreAuthorize("hasAnyRole('READER', 'USER', 'ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> getRegistration(@PathVariable Long eventId) {
        log.info("Get registration details - Event ID: {}", eventId);
        EventRegistrationDto registration = eventRegistrationService.getRegistration(eventId, getCurrentUserId());
        
        if (registration != null) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Registration details retrieved")
                    .data(registration)
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder()
                    .success(false)
                    .message("Registration not found")
                    .build());
        }
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
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
