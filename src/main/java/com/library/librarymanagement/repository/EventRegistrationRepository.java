package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.EventRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    
    /**
     * Find all registrations for a specific event
     */
    @Query("SELECT er FROM EventRegistration er WHERE er.event.id = :eventId AND er.isDeleted = false ORDER BY er.registeredDate DESC")
    Page<EventRegistration> findByEventId(@Param("eventId") Long eventId, Pageable pageable);
    
    /**
     * Find all registrations for a specific user
     */
    @Query("SELECT er FROM EventRegistration er WHERE er.account.id = :accountId AND er.isDeleted = false ORDER BY er.registeredDate DESC")
    Page<EventRegistration> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);
    
    /**
     * Check if user is already registered for an event
     */
    @Query("SELECT COUNT(er) > 0 FROM EventRegistration er WHERE er.event.id = :eventId AND er.account.id = :accountId AND er.isDeleted = false")
    boolean isUserRegisteredForEvent(@Param("eventId") Long eventId, @Param("accountId") Long accountId);
    
    /**
     * Find a specific registration
     */
    @Query("SELECT er FROM EventRegistration er WHERE er.event.id = :eventId AND er.account.id = :accountId AND er.isDeleted = false")
    Optional<EventRegistration> findRegistration(@Param("eventId") Long eventId, @Param("accountId") Long accountId);
    
    /**
     * Count registrations for an event
     */
    @Query("SELECT COUNT(er) FROM EventRegistration er WHERE er.event.id = :eventId AND er.status = 'REGISTERED' AND er.isDeleted = false")
    int countRegistrationsByEventId(@Param("eventId") Long eventId);
}
