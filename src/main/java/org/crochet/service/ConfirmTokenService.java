package org.crochet.service;

import org.crochet.model.ConfirmationToken;
import org.crochet.model.User;

import java.time.LocalDateTime;

public interface ConfirmTokenService {
    ConfirmationToken createOrUpdate(User user);

    void updateConfirmedAt(String token, LocalDateTime dateTime);

    ConfirmationToken getToken(String token);

    void deleteExpiredOrConfirmedTokens();
}
