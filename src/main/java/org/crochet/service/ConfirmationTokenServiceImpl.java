package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.ConfirmationTokenMapper;
import org.crochet.mapper.UserMapper;
import org.crochet.model.ConfirmationToken;
import org.crochet.model.User;
import org.crochet.repository.ConfirmationTokenRepository;
import org.crochet.response.ConfirmationTokenResponse;
import org.crochet.response.UserResponse;
import org.crochet.service.abstraction.ConfirmationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

  private final ConfirmationTokenRepository repository;

  @Autowired
  public ConfirmationTokenServiceImpl(ConfirmationTokenRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional
  public ConfirmationTokenResponse createOrUpdate(UserResponse userResponse) {
    User user = UserMapper.INSTANCE.toUser(userResponse);

    ConfirmationToken confirmationToken = repository
        .findByUser(user)
        .orElse(null);

    if (confirmationToken == null) {
      // Create a new token
      String token = UUID.randomUUID().toString();
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime expirationTime = now.plusMinutes(15);

      confirmationToken = new ConfirmationToken()
          .setToken(token)
          .setCreatedAt(now)
          .setExpiresAt(expirationTime)
          .setUser(user);
    } else {
      // Update the existing token
      confirmationToken.setToken(UUID.randomUUID().toString())
          .setCreatedAt(LocalDateTime.now())
          .setExpiresAt(LocalDateTime.now().plusMinutes(15));
    }

    confirmationToken = repository.save(confirmationToken);

    return ConfirmationTokenMapper.INSTANCE.toConfirmationTokenResponse(confirmationToken);
  }

  @Override
  public ConfirmationTokenResponse getToken(String token) {
    var confirmationToken = repository.findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
    return ConfirmationTokenMapper.INSTANCE.toConfirmationTokenResponse(confirmationToken);
  }

  @Override
  public void updateConfirmedAt(String token) {
    repository.updateConfirmedAt(token, LocalDateTime.now());
  }
}
