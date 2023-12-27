package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.PasswordResetToken;
import org.crochet.model.User;
import org.crochet.repository.PasswordResetTokenRepository;
import org.crochet.service.contact.PasswordResetTokenService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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

    String token = UUID.randomUUID().toString();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expirationTime = now.plusMinutes(15);

    if (passwordResetToken == null) {
      // Create a new token
      passwordResetToken = PasswordResetToken.builder()
          .token(token)
          .createdAt(now)
          .expiresAt(expirationTime)
          .user(user)
          .build();
    } else {
      passwordResetToken.setToken(token);
      passwordResetToken.setCreatedAt(now);
      passwordResetToken.setExpiresAt(expirationTime);
    }

    return passwordResetTokenRepository.save(passwordResetToken);
  }

  @Override
  public String getEmailByToken(String token) {
    return passwordResetTokenRepository.findEmailByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with token: " + token));
  }

  @Override
  public void deletePasswordToken(PasswordResetToken token) {
    passwordResetTokenRepository.delete(token);
  }

  @Override
  public PasswordResetToken getPasswordResetToken(String token) {
    return passwordResetTokenRepository.findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Password reset token not found"));
  }
}
