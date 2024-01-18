package org.crochet.service.contact;

import org.crochet.model.PasswordResetToken;
import org.crochet.model.User;

public interface PasswordResetTokenService {
    PasswordResetToken createOrUpdatePasswordResetToken(User user);

    String getEmailByToken(String token);

    void deletePasswordToken(PasswordResetToken token);

    PasswordResetToken getPasswordResetToken(String token);
}
