package com.library.librarymanagement.service.refresh_token;

import com.library.librarymanagement.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken saveRefreshToken(String username, String refreshToken);
    RefreshToken verifyExpiration(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void revoke(String token);
}
