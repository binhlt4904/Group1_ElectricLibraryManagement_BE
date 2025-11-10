package com.library.librarymanagement.repository.publisher;

import com.library.librarymanagement.entity.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    Optional<Publisher> findByCompanyName(String companyName);


    Page<Publisher> findAll(Pageable pageable);

    @Query("SELECT p FROM Publisher p " +
            "WHERE LOWER(p.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Publisher> searchPublishers(String keyword, Pageable pageable);
}
