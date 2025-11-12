package com.library.librarymanagement.repository.reset_token;

import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByAccountAndUsedFalse(Account account);
}
