package org.crochet.service;

import org.crochet.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username);

    Optional<RefreshToken> findByToken(String token);

    RefreshToken verifyExpiration(RefreshToken token);

    void revokeByToken(String token);

    void deleteExpiredRefreshTokens();
}
