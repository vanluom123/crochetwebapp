package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.ConfirmationToken;
import org.crochet.model.User;
import org.crochet.repository.ConfirmationTokenRepository;
import org.crochet.service.ConfirmTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.CONFIRM_TOKEN_NOT_FOUND_MESSAGE;

@Service
public class ConfirmTokenServiceImpl implements ConfirmTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

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
            confirmationToken = new ConfirmationToken()
                    .setToken(token)
                    .setExpiresAt(expirationTime)
                    .setUser(user);
        } else {
            // Update the existing token
            confirmationToken.setExpiresAt(expirationTime);
        }
        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public void updateConfirmedAt(String token, LocalDateTime dateTime) {
        confirmationTokenRepository.updateConfirmedAt(token, dateTime);
    }

    @Override
    public ConfirmationToken getToken(String token) {
        return confirmationTokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException(CONFIRM_TOKEN_NOT_FOUND_MESSAGE,
                                MAP_CODE.get(CONFIRM_TOKEN_NOT_FOUND_MESSAGE))
                );
    }
}
