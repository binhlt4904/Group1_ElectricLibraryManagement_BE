package com.library.librarymanagement.repository.wallet;

import com.library.librarymanagement.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByReader_Id(Long readerId);
}
