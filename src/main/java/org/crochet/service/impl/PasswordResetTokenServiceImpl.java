package org.crochet.service.impl;

import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.PasswordResetToken;
import org.crochet.model.User;
import org.crochet.repository.PasswordResetTokenRepository;
import org.crochet.service.PasswordResetTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * Constructor
     *
     * @param passwordResetTokenRepository PasswordResetTokenRepository
     */
    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    /**
     * Create or update password reset token
     *
     * @param user User
     * @return PasswordResetToken
     */
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

    /**
     * Get email by token
     *
     * @param token String
     * @return String
     */
    @Override
    public String getEmailByToken(String token) {
        return passwordResetTokenRepository.findEmailByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ResultCode.MSG_USER_NOT_FOUND_WITH_TOKEN.message(),
                                ResultCode.MSG_USER_NOT_FOUND_WITH_TOKEN.code())
                );
    }

    /**
     * Delete password reset token
     *
     * @param token PasswordResetToken
     */
    @Override
    public void deletePasswordToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }

    /**
     * Get password reset token
     *
     * @param token String
     * @return PasswordResetToken
     */
    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ResultCode.MSG_PASSWORD_RESET_TOKEN_NOT_FOUND.message(),
                                ResultCode.MSG_PASSWORD_RESET_TOKEN_NOT_FOUND.code())
                );
    }
}
