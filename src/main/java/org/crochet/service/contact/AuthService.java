package org.crochet.service.contact;

import org.crochet.request.LoginRequest;
import org.crochet.request.PasswordResetRequest;
import org.crochet.request.SignUpRequest;
import org.crochet.response.EntityResponse;
import org.crochet.response.AuthResponse;

public interface AuthService {
    AuthResponse authenticateUser(LoginRequest loginRequest);

    EntityResponse registerUser(SignUpRequest signUpRequest);

    EntityResponse resendVerificationEmail(String email);

    EntityResponse confirmToken(String token);

    EntityResponse resetPasswordLink(String email);

    EntityResponse resetPassword(String token, PasswordResetRequest passwordResetRequest);
}
