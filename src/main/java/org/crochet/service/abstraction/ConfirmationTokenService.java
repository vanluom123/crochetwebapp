package org.crochet.service.abstraction;

import org.crochet.request.UserRequest;
import org.crochet.response.ConfirmationTokenResponse;

public interface ConfirmationTokenService {
  ConfirmationTokenResponse createOrUpdate(UserRequest userRequest);

  ConfirmationTokenResponse createOrUpdate(String email);

  ConfirmationTokenResponse getToken(String token);
  void updateConfirmedAt(String token);
}
