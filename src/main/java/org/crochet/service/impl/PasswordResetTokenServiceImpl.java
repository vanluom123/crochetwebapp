package org.crochet.service.impl;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.PasswordResetToken;
import org.crochet.model.User;
import org.crochet.repository.PasswordResetTokenRepository;
import org.crochet.service.PasswordResetTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.PASSWORD_RESET_TOKEN_NOT_FOUND_MESSAGE;
import static org.crochet.constant.MessageConstant.USER_NOT_FOUND_WITH_TOKEN_MESSAGE;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public PasswordResetToken createOrUpdatePasswordResetToken(User user) {
        var passwordResetToken = passwordResetTokenRepository.findByUser(user)
                .orElse(null);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(15);
        if (passwordResetToken == null) {
            // Create a new token
            String token = UUID.randomUUID().toString();
            passwordResetToken = PasswordResetToken.builder()
                    .token(token)
                    .expiresAt(expirationTime)
                    .user(user)
                    .build();
        } else {
            passwordResetToken.setExpiresAt(expirationTime);
        }
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String getEmailByToken(String token) {
        return passwordResetTokenRepository.findEmailByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException(USER_NOT_FOUND_WITH_TOKEN_MESSAGE + token,
                                MAP_CODE.get(USER_NOT_FOUND_WITH_TOKEN_MESSAGE))
                );
    }

    @Override
    public void deletePasswordToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException(PASSWORD_RESET_TOKEN_NOT_FOUND_MESSAGE,
                                MAP_CODE.get(PASSWORD_RESET_TOKEN_NOT_FOUND_MESSAGE))
                );
    }
}
