package org.crochet.service.impl;

import org.crochet.properties.AppProperties;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.RefreshToken;
import org.crochet.model.User;
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

    public RefreshTokenServiceImpl(RefreshTokenRepo refreshTokenRepo,
                                   UserRepository userRepository,
                                   AppProperties appProps) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepository = userRepository;
        this.appProps = appProps;
    }

    @Transactional
    @Override
    public RefreshToken createRefreshToken(String username) {
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        LocalDateTime now = LocalDateTime.now();
        var expiryDate = now.plus(appProps.getAuth().getRefreshTokenExpirationMs(), ChronoUnit.MILLIS);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(expiryDate) // set expiry of refresh token to 10 minutes - you can configure it application.properties file
                .build();
        revokeRefreshToken(user);
        return refreshTokenRepo.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepo.delete(token);
            throw new ResourceNotFoundException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

    @Override
    public void revokeByToken(String token) {
        var refreshToken = findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found: " + token));
        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
    }

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
}
