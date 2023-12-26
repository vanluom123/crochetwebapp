package org.crochet.service.contact;

import org.crochet.payload.request.LoginRequest;
import org.crochet.payload.request.PasswordResetRequest;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.response.EntityResponse;
import org.crochet.payload.response.AuthResponse;

public interface AuthService {
    AuthResponse authenticateUser(LoginRequest loginRequest);

    EntityResponse registerUser(SignUpRequest signUpRequest);

    EntityResponse resendVerificationEmail(String email);

    EntityResponse confirmToken(String token);

    EntityResponse resetPasswordLink(String email);

    EntityResponse resetPassword(String token, PasswordResetRequest passwordResetRequest);
}
