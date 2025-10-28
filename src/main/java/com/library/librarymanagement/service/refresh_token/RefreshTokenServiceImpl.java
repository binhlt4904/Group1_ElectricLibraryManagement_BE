package com.library.librarymanagement.service.refresh_token;

import com.library.librarymanagement.entity.Account;
import com.library.librarymanagement.entity.RefreshToken;
import com.library.librarymanagement.repository.account.AccountRepository;
import com.library.librarymanagement.repository.refresh_token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshTokenDuration;

    @Override
    public RefreshToken saveRefreshToken(String username, String refreshToken) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        RefreshToken token = RefreshToken.builder()
                .account(account)
                .token(refreshToken)
                .expiryDate(Instant.now().plusMillis(refreshTokenDuration))
                .build();
        return refreshTokenRepository.save(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.isExpired()){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Expired refresh token");
        }
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public void revoke(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
    }
}
