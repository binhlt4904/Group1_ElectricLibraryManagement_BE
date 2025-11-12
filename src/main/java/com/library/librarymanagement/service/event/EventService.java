package com.library.librarymanagement.service.event;

import com.library.librarymanagement.dto.request.CreateEventRequest;
import com.library.librarymanagement.dto.request.UpdateEventRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.EventDto;
import org.springframework.data.domain.Page;

public interface EventService {
    
    /**
     * Get all events with pagination (public access)
     */
    Page<EventDto> getAllEvents(String title, int page, int size);
    
    /**
     * Get event by ID (public access)
     */
    EventDto getEventById(Long id);
    
    /**
     * Create new event (admin only)
     */
    ApiResponse createEvent(CreateEventRequest request);
    
    /**
     * Update event (admin only)
     */
    ApiResponse updateEvent(Long id, UpdateEventRequest request);
    
    /**
     * Delete event (admin only)
     */
    ApiResponse deleteEvent(Long id);
    
    /**
     * Get event statistics (admin only)
     * Returns aggregated statistics: upcoming events, total registrations, ongoing events, avg attendance
     */
    ApiResponse getEventStatistics();
    
    /**
     * Auto-update event status based on current date/time
     * Scheduled task to update event statuses: upcoming -> ongoing -> completed
     */
    void autoUpdateEventStatus();
}
