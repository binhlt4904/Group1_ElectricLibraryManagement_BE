package com.library.librarymanagement.repository;

import com.library.librarymanagement.entity.CardRenewalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRenewalDetailsRepository extends JpaRepository<CardRenewalDetails, Long> {
    
    /**
     * Find all renewal requests for a card number
     */
    @Query("SELECT crd FROM CardRenewalDetails crd WHERE crd.cardCode = :cardCode ORDER BY crd.createdDate DESC")
    List<CardRenewalDetails> findByCardCode(@Param("cardCode") String cardCode);
    
    /**
     * Find the most recent renewal request for a card
     */
    @Query("SELECT crd FROM CardRenewalDetails crd WHERE crd.cardCode = :cardCode ORDER BY crd.createdDate DESC")
    List<CardRenewalDetails> findRecentRenewalsByCardCode(@Param("cardCode") String cardCode);
}
