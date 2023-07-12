package org.crochet.mapper.abstraction;

import org.crochet.model.ConfirmationToken;
import org.crochet.response.ConfirmationTokenResponse;

public interface ConfirmationTokenMapper {
  ConfirmationTokenResponse toConfirmationTokenResponse(ConfirmationToken confirmationToken);
}
