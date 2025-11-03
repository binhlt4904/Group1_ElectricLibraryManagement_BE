package com.library.librarymanagement.controller.admin;

import com.library.librarymanagement.dto.request.CreateEventRequest;
import com.library.librarymanagement.dto.request.UpdateEventRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.service.event.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/events")
@Slf4j
public class AdminEventController {
    
    private final EventService eventService;
    
    /**
     * Create new event
     * POST /api/v1/admin/events
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request
    ) {
        log.info("Create event request: {}", request.getTitle());
        ApiResponse response = eventService.createEvent(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Update event
     * PUT /api/v1/admin/events/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        log.info("Update event request for ID: {}", id);
        ApiResponse response = eventService.updateEvent(id, request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Delete event
     * DELETE /api/v1/admin/events/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse> deleteEvent(@PathVariable Long id) {
        log.info("Delete event request for ID: {}", id);
        ApiResponse response = eventService.deleteEvent(id);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
