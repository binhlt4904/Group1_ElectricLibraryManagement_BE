package com.library.librarymanagement.service.event;

import com.library.librarymanagement.dto.request.EventRegistrationRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.EventRegistrationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRegistrationService {
    
    /**
     * Register a user for an event
     */
    ApiResponse registerForEvent(Long eventId, EventRegistrationRequest request);
    
    /**
     * Cancel registration for an event
     */
    ApiResponse cancelRegistration(Long eventId);
    
    /**
     * Get all registrations for an event (admin only)
     */
    Page<EventRegistrationDto> getEventRegistrations(Long eventId, Pageable pageable);
    
    /**
     * Get all registrations for a user
     */
    Page<EventRegistrationDto> getUserRegistrations(Long userId, Pageable pageable);
    
    /**
     * Check if user is registered for an event
     */
    boolean isUserRegistered(Long eventId, Long userId);
    
    /**
     * Get registration details
     */
    EventRegistrationDto getRegistration(Long eventId, Long userId);
}
