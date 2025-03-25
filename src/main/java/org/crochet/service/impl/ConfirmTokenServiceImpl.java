package org.crochet.service.impl;

import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.ConfirmationToken;
import org.crochet.model.User;
import org.crochet.repository.ConfirmationTokenRepository;
import org.crochet.service.ConfirmTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmTokenServiceImpl implements ConfirmTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    /**
     * Constructor
     *
     * @param confirmationTokenRepository the confirmationTokenRepository
     */
    public ConfirmTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    /**
     * Create or update a confirmation token
     *
     * @param user the user
     * @return the confirmation token
     */
    @Override
    public ConfirmationToken createOrUpdate(User user) {
        ConfirmationToken confirmationToken = confirmationTokenRepository
                .findByUser(user)
                .orElse(null);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(15);
        if (confirmationToken == null) {
            // Create a new token
            String token = UUID.randomUUID().toString();
            confirmationToken = ConfirmationToken.builder()
                    .token(token)
                    .expiresAt(expirationTime)
                    .user(user)
                    .build();
        } else {
            // Update the existing token
            confirmationToken.setExpiresAt(expirationTime);
        }
        return confirmationTokenRepository.save(confirmationToken);
    }

    /**
     * Update the confirmed at time of a token
     *
     * @param token    the token
     * @param dateTime the date time
     */
    @Override
    public void updateConfirmedAt(String token, LocalDateTime dateTime) {
        confirmationTokenRepository.updateConfirmedAt(token, dateTime);
    }

    /**
     * Get a token by token string
     *
     * @param token the token string
     * @return the token
     */
    @Override
    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ResultCode.MSG_CONFIRM_TOKEN_NOT_FOUND.message(),
                                ResultCode.MSG_CONFIRM_TOKEN_NOT_FOUND.code()
                        ));
    }

    /**
     * Delete expired or confirmed tokens
     */
    @Transactional
    @Override
    public void deleteExpiredOrConfirmedTokens() {
        var tokens = confirmationTokenRepository.findAll()
                .stream()
                .filter(token -> token.getExpiresAt().isBefore(LocalDateTime.now()) || token.getConfirmedAt() != null)
                .toList();
        confirmationTokenRepository.deleteAll(tokens);
    }
}
