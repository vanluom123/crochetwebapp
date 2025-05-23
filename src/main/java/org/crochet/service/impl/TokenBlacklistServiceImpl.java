package org.crochet.service.impl;

import org.crochet.model.TokenBlacklist;
import org.crochet.repository.TokenBlacklistRepo;
import org.crochet.service.TokenBlacklistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    private final TokenBlacklistRepo tokenBlacklistRepo;

    /**
     * Constructor
     *
     * @param tokenBlacklistRepo TokenBlacklistRepo
     */
    public TokenBlacklistServiceImpl(TokenBlacklistRepo tokenBlacklistRepo) {
        this.tokenBlacklistRepo = tokenBlacklistRepo;
    }

    /**
     * Add a token to the blacklist
     *
     * @param token String
     */
    @Transactional
    @Override
    public void addTokenToBlacklist(String token) {
        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(token);
        tokenBlacklist.setBlacklistedAt(LocalDateTime.now());
        tokenBlacklistRepo.save(tokenBlacklist);
    }

    /**
     * Check if a token is blacklisted
     *
     * @param token String
     * @return boolean
     */
    @Override
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepo.findByToken(token).isPresent();
    }
}
