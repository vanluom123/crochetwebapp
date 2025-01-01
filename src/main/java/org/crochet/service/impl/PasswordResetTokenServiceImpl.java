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
import static org.crochet.constant.MessageConstant.MSG_PASSWORD_RESET_TOKEN_NOT_FOUND;
import static org.crochet.constant.MessageConstant.MSG_USER_NOT_FOUND_WITH_TOKEN;

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
                        new ResourceNotFoundException(MSG_USER_NOT_FOUND_WITH_TOKEN + token,
                                MAP_CODE.get(MSG_USER_NOT_FOUND_WITH_TOKEN))
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
                        new ResourceNotFoundException(MSG_PASSWORD_RESET_TOKEN_NOT_FOUND,
                                MAP_CODE.get(MSG_PASSWORD_RESET_TOKEN_NOT_FOUND))
                );
    }
}
