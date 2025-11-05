package com.library.librarymanagement.repository.wallet;

import com.library.librarymanagement.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByReaderId(Long readerId);
    Wallet findByReader_Id(Long readerId);
}
