package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.LibraryCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryCardRepository extends JpaRepository<LibraryCard, Long> {
    
    /**
     * Find library card by reader ID
     */
    @Query("SELECT lc FROM LibraryCard lc WHERE lc.reader.id = :readerId AND (lc.isDeleted = false OR lc.isDeleted IS NULL)")
    Optional<LibraryCard> findByReaderId(@Param("readerId") Long readerId);
    
    /**
     * Find library card by card number
     */
    @Query("SELECT lc FROM LibraryCard lc WHERE lc.cardNumber = :cardNumber AND (lc.isDeleted = false OR lc.isDeleted IS NULL)")
    Optional<LibraryCard> findByCardNumber(@Param("cardNumber") String cardNumber);
    
    /**
     * Check if card number exists
     */
    boolean existsByCardNumber(String cardNumber);
}
