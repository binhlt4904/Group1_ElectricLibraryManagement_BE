package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Find all events with pagination
     */
    Page<Event> findAll(Pageable pageable);
    
    /**
     * Search events by title with eager fetching of relationships
     */
    @Query("SELECT e FROM Event e " +
           "LEFT JOIN FETCH e.fromUser su " +
           "LEFT JOIN FETCH su.account a " +
           "WHERE (:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%')))")
    Page<Event> searchEvents(@Param("title") String title, Pageable pageable);
}
