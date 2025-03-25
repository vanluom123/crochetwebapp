package org.crochet.service.impl;

import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.exception.TokenException;
import org.crochet.model.RefreshToken;
import org.crochet.model.User;
import org.crochet.properties.AppProperties;
import org.crochet.repository.RefreshTokenRepo;
import org.crochet.repository.UserRepository;
import org.crochet.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepository userRepository;
    private final AppProperties appProps;

    /**
     * Constructor for RefreshTokenServiceImpl
     *
     * @param refreshTokenRepo RefreshTokenRepo
     * @param userRepository    UserRepository
     * @param appProps         AppProperties
     */
    public RefreshTokenServiceImpl(RefreshTokenRepo refreshTokenRepo,
                                   UserRepository userRepository,
                                   AppProperties appProps) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepository = userRepository;
        this.appProps = appProps;
    }

    /**
     * Create a refresh token for a user
     *
     * @param username String
     * @return RefreshToken
     */
    @Transactional
    @Override
    public RefreshToken createRefreshToken(String username) {
        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResultCode.MSG_USER_NOT_FOUND_WITH_EMAIL.message(),
                        ResultCode.MSG_USER_NOT_FOUND_WITH_EMAIL.code()));
        LocalDateTime now = LocalDateTime.now();
        var expiryDate = now.plus(appProps.getAuth().getRefreshTokenExpirationMs(), ChronoUnit.MILLIS);
        var refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiresAt(expiryDate)
                .user(user)
                .build();
        revokeRefreshToken(user);
        return refreshTokenRepo.save(refreshToken);
    }

    /**
     * Delete a refresh token
     *
     * @param token RefreshToken
     */
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    /**
     * Delete a refresh token
     *
     * @param token RefreshToken
     */
    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now()) || token.isRevoked()) {
            refreshTokenRepo.delete(token);
            throw new TokenException(token.getToken() + ResultCode.MSG_REFRESH_TOKEN_EXPIRED.message(),
                    ResultCode.MSG_REFRESH_TOKEN_EXPIRED.code());
        }
        return token;
    }

    /**
     * Delete a refresh token
     *
     * @param token RefreshToken
     */
    @Override
    public void revokeByToken(String token) {
        var refreshToken = findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(ResultCode.MSG_REFRESH_TOKEN_NOT_FOUND.message(),
                        ResultCode.MSG_REFRESH_TOKEN_NOT_FOUND.code()));
        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
    }

    /**
     * Delete a refresh token
     *
     * @param user User
     */
    private void revokeRefreshToken(User user) {
        var validRefreshTokens = refreshTokenRepo.findAllValidRefreshTokenByUser(user);
        if (validRefreshTokens.isEmpty()) {
            return;
        }
        validRefreshTokens.forEach(token -> {
            token.setRevoked(true);
            refreshTokenRepo.save(token);
        });
    }

    /**
     * Delete expired and revoked refresh tokens
     */
    @Override
    @Transactional
    public void deleteExpiredRefreshTokens() {
        var refreshTokens = refreshTokenRepo.findAll()
                .stream()
                .filter(RefreshToken::isRevoked)
                .filter(token -> token.getExpiresAt().isBefore(LocalDateTime.now()))
                .toList();
        refreshTokenRepo.deleteAll(refreshTokens);
    }
}
