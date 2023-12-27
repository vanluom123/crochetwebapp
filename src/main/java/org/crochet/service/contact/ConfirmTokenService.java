package org.crochet.service.contact;

import org.crochet.model.ConfirmationToken;
import org.crochet.model.User;

import java.time.LocalDateTime;

public interface ConfirmTokenService {
    ConfirmationToken createOrUpdateToken(User user);

    void updateConfirmedAt(String token, LocalDateTime dateTime);

    ConfirmationToken getToken(String token);
}
