package org.crochet.service.impl;

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

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.REFRESH_TOKEN_IS_EXPIRED_MESSAGE;
import static org.crochet.constant.MessageConstant.REFRESH_TOKEN_NOT_FOUND_MESSAGE;
import static org.crochet.constant.MessageConstant.USER_NOT_FOUND_WITH_EMAIL_MESSAGE;

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
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_EMAIL_MESSAGE + username));
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

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepo.delete(token);
            throw new TokenException(token.getToken() + REFRESH_TOKEN_IS_EXPIRED_MESSAGE,
                    MAP_CODE.get(REFRESH_TOKEN_IS_EXPIRED_MESSAGE));
        }
        return token;
    }

    @Override
    public void revokeByToken(String token) {
        var refreshToken = findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(REFRESH_TOKEN_NOT_FOUND_MESSAGE + token,
                        MAP_CODE.get(REFRESH_TOKEN_NOT_FOUND_MESSAGE)));
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
