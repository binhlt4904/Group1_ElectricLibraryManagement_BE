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

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    
    private final EventRepository eventRepository;
    private final SystemUserRepository systemUserRepository;
    
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
            
            // Get SystemUser for the current account
            // If SystemUser doesn't exist, create one automatically for admin/librarian
            SystemUser systemUser = systemUserRepository.findByAccountId(userId)
                    .orElseGet(() -> {
                        log.info("SystemUser not found for account ID: {}. Creating one...", userId);
                        SystemUser newSystemUser = new SystemUser();
                        newSystemUser.setAccount(new Account());
                        newSystemUser.getAccount().setId(userId);
                        newSystemUser.setJoinDate(new java.sql.Timestamp(System.currentTimeMillis()));
                        newSystemUser.setCreatedDate(new java.sql.Timestamp(System.currentTimeMillis()));
                        newSystemUser.setIsDeleted(false);
                        return systemUserRepository.save(newSystemUser);
                    });
            
            Event event = new Event();
            event.setTitle(request.getTitle());
            event.setDescription(request.getDescription());
            event.setCreatedDate(new Date());
            event.setFromUser(systemUser);
            
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
            
            if (request.getTitle() != null && !request.getTitle().isBlank()) {
                event.setTitle(request.getTitle());
            }
            
            if (request.getDescription() != null && !request.getDescription().isBlank()) {
                event.setDescription(request.getDescription());
            }
            
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
    
    private EventDto convertToDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .createdDate(event.getCreatedDate())
                .createdBy(event.getFromUser() != null ? event.getFromUser().getId() : null)
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
