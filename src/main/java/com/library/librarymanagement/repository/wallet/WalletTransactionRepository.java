package com.library.librarymanagement.repository.wallet;

import com.library.librarymanagement.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> , JpaSpecificationExecutor<WalletTransaction> {
    Optional<WalletTransaction> findFirstByWalletIdAndStatusOrderByCreatedDate(Long walletId, String status);

    List<WalletTransaction> findByStatus(String status);

    List<WalletTransaction> findByWalletIdAndType(Long walletId, String type);
}
