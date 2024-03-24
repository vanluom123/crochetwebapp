package org.crochet.service.impl;

import org.crochet.properties.MessageCodeProperties;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.ConfirmationToken;
import org.crochet.model.User;
import org.crochet.repository.ConfirmationTokenRepository;
import org.crochet.service.ConfirmTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.crochet.constant.MessageConstant.CONFIRM_TOKEN_NOT_FOUND_MESSAGE;

@Service
public class ConfirmTokenServiceImpl implements ConfirmTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final MessageCodeProperties msgCodeProps;

    public ConfirmTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository,
                                   MessageCodeProperties msgCodeProps) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.msgCodeProps = msgCodeProps;
    }

    @Override
    public ConfirmationToken createOrUpdateToken(User user) {
        ConfirmationToken confirmationToken = confirmationTokenRepository
                .findByUser(user)
                .orElse(null);

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(15);

        if (confirmationToken == null) {
            // Create a new token
            confirmationToken = ConfirmationToken.builder()
                    .token(token)
                    .createdAt(now)
                    .expiresAt(expirationTime)
                    .user(user)
                    .build();
        } else {
            // Update the existing token
            confirmationToken.setToken(token);
            confirmationToken.setCreatedAt(now);
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
        return confirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(CONFIRM_TOKEN_NOT_FOUND_MESSAGE,
                        msgCodeProps.getCode("CONFIRM_TOKEN_NOT_FOUND_MESSAGE")));
    }
}
