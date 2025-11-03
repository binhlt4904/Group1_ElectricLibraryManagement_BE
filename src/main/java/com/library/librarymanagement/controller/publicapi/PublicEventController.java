package com.library.librarymanagement.controller.publicapi;

import com.library.librarymanagement.dto.response.EventDto;
import com.library.librarymanagement.service.event.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/events")
@Slf4j
public class PublicEventController {
    
    private final EventService eventService;
    
    /**
     * Get all events with pagination
     * GET /api/v1/public/events
     */
    @GetMapping
    public ResponseEntity<Page<EventDto>> getAllEvents(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Get all events request - title: {}, page: {}, size: {}", title, page, size);
        Page<EventDto> events = eventService.getAllEvents(title, page, size);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get event by ID
     * GET /api/v1/public/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable Long id) {
        log.info("Get event by ID: {}", id);
        EventDto event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }
}
