package org.crochet.service.abstraction;

import org.crochet.response.ConfirmationTokenResponse;
import org.crochet.response.UserResponse;

public interface ConfirmationTokenService {
  ConfirmationTokenResponse createOrUpdate(UserResponse userResponse);
  ConfirmationTokenResponse getToken(String token);
  void updateConfirmedAt(String token);
}
