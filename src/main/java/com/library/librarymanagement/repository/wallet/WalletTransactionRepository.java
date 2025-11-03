package com.library.librarymanagement.repository.wallet;

import com.library.librarymanagement.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    Optional<WalletTransaction> findFirstByWalletIdAndStatusOrderByCreatedDate(Long walletId, String status);
}
