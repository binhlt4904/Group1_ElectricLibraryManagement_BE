package com.library.librarymanagement.service.event;

import com.library.librarymanagement.dto.request.EventRegistrationRequest;
import com.library.librarymanagement.dto.response.ApiResponse;
import com.library.librarymanagement.dto.response.EventRegistrationDto;
import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.Event;
import com.library.librarymanagement.entity.EventRegistration;
import com.library.librarymanagement.exception.ObjectNotExistException;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.repository.EventRegistrationRepository;
import com.library.librarymanagement.repository.EventRepository;
import com.library.librarymanagement.service.custom_user_details.CustomUserDetails;
import com.library.librarymanagement.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRegistrationServiceImpl implements EventRegistrationService {
    
    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventRepository eventRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    
    @Override
    @Transactional
    public ApiResponse registerForEvent(Long eventId, EventRegistrationRequest request) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ApiResponse.builder()
                        .success(false)
                        .message("User not authenticated")
                        .build();
            }
            
            // Check if event exists
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ObjectNotExistException("Event not found with ID: " + eventId));
            
            // Check if user already registered
            if (eventRegistrationRepository.isUserRegisteredForEvent(eventId, userId)) {
                return ApiResponse.builder()
                        .success(false)
                        .message("User is already registered for this event")
                        .build();
            }
            
            // Check if event is full
            int registeredCount = eventRegistrationRepository.countRegistrationsByEventId(eventId);
            if (event.getCapacity() != null && registeredCount >= event.getCapacity()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Event is full")
                        .build();
            }
            
            // Get account
            Account account = accountRepository.findById(userId)
                    .orElseThrow(() -> new ObjectNotExistException("Account not found"));
            
            // Create registration
            EventRegistration registration = EventRegistration.builder()
                    .event(event)
                    .account(account)
                    .fullName(request.getFullName() != null ? request.getFullName() : account.getFullName())
                    .email(request.getEmail() != null ? request.getEmail() : account.getEmail())
                    .phone(request.getPhone())
                    .specialRequests(request.getSpecialRequests())
                    .status("REGISTERED")
                    .registeredDate(new Timestamp(System.currentTimeMillis()))
                    .isDeleted(false)
                    .build();
            
            EventRegistration saved = eventRegistrationRepository.save(registration);
            
            // Update event registered count
            event.setRegistered((event.getRegistered() != null ? event.getRegistered() : 0) + 1);
            eventRepository.save(event);
            
            log.info("✅ User {} registered for event {}", userId, eventId);
            
            // Send registration confirmation notification
            try {
                notificationService.sendNotification(
                    com.library.librarymanagement.dto.request.SendNotificationRequest.builder()
                        .title("Event Registration Confirmed")
                        .description("You have successfully registered for the event: " + event.getTitle())
                        .notificationType("EVENT_REGISTERED")
                        .toUserId(userId)
                        .relatedEventId(eventId)
                        .relatedBookId(null)
                        .relatedBorrowRecordId(null)
                        .build()
                );
                log.info("✅ Registration confirmation notification sent to user: {}", userId);
            } catch (Exception notifyEx) {
                log.error("Failed to send registration confirmation notification: {}", notifyEx.getMessage());
            }
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Successfully registered for event")
                    .data(java.util.Map.of("registrationId", saved.getId()))
                    .build();
                    
        } catch (Exception e) {
            log.error("Error registering for event", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error registering for event: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    @Transactional
    public ApiResponse cancelRegistration(Long eventId) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ApiResponse.builder()
                        .success(false)
                        .message("User not authenticated")
                        .build();
            }
            
            // Find registration
            Optional<EventRegistration> registrationOpt = eventRegistrationRepository.findRegistration(eventId, userId);
            if (!registrationOpt.isPresent()) {
                return ApiResponse.builder()
                        .success(false)
                        .message("Registration not found")
                        .build();
            }
            
            EventRegistration registration = registrationOpt.get();
            registration.setIsDeleted(true);
            registration.setStatus("CANCELLED");
            eventRegistrationRepository.save(registration);
            
            // Update event registered count
            Event event = eventRepository.findById(eventId).orElse(null);
            if (event != null) {
                event.setRegistered(Math.max(0, (event.getRegistered() != null ? event.getRegistered() : 0) - 1));
                eventRepository.save(event);
            }
            
            log.info("✅ User {} cancelled registration for event {}", userId, eventId);
            
            // Send cancellation notification
            try {
                String eventTitle = event != null ? event.getTitle() : "Unknown Event";
                notificationService.sendNotification(
                    com.library.librarymanagement.dto.request.SendNotificationRequest.builder()
                        .title("Event Registration Cancelled")
                        .description("Your registration for the event: " + eventTitle + " has been cancelled.")
                        .notificationType("EVENT_CANCELLED")
                        .toUserId(userId)
                        .relatedEventId(eventId)
                        .relatedBookId(null)
                        .relatedBorrowRecordId(null)
                        .build()
                );
                log.info("✅ Cancellation notification sent to user: {}", userId);
            } catch (Exception notifyEx) {
                log.error("Failed to send cancellation notification: {}", notifyEx.getMessage());
            }
            
            return ApiResponse.builder()
                    .success(true)
                    .message("Successfully cancelled registration")
                    .build();
                    
        } catch (Exception e) {
            log.error("Error cancelling registration", e);
            return ApiResponse.builder()
                    .success(false)
                    .message("Error cancelling registration: " + e.getMessage())
                    .build();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<EventRegistrationDto> getEventRegistrations(Long eventId, Pageable pageable) {
        Page<EventRegistration> registrations = eventRegistrationRepository.findByEventId(eventId, pageable);
        return registrations.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<EventRegistrationDto> getUserRegistrations(Long userId, Pageable pageable) {
        Page<EventRegistration> registrations = eventRegistrationRepository.findByAccountId(userId, pageable);
        return registrations.map(this::convertToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isUserRegistered(Long eventId, Long userId) {
        return eventRegistrationRepository.isUserRegisteredForEvent(eventId, userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public EventRegistrationDto getRegistration(Long eventId, Long userId) {
        Optional<EventRegistration> registration = eventRegistrationRepository.findRegistration(eventId, userId);
        return registration.map(this::convertToDto).orElse(null);
    }
    
    private EventRegistrationDto convertToDto(EventRegistration registration) {
        return EventRegistrationDto.builder()
                .id(registration.getId())
                .eventId(registration.getEvent().getId())
                .eventTitle(registration.getEvent().getTitle())
                .accountId(registration.getAccount().getId())
                .username(registration.getAccount().getUsername())
                .fullName(registration.getFullName())
                .email(registration.getEmail())
                .phone(registration.getPhone())
                .specialRequests(registration.getSpecialRequests())
                .status(registration.getStatus())
                .registeredDate(registration.getRegisteredDate())
                .build();
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
