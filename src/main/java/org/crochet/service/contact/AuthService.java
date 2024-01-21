package org.crochet.service.contact;

import jakarta.servlet.http.HttpServletRequest;
import org.crochet.payload.request.LoginRequest;
import org.crochet.payload.request.PasswordResetRequest;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.response.AuthResponse;
import org.crochet.payload.response.TokenResponse;

public interface AuthService {
    AuthResponse authenticateUser(LoginRequest loginRequest);

    String registerUser(SignUpRequest signUpRequest);

    String resendVerificationEmail(String email);

    String confirmToken(String token);

    String resetPasswordLink(String email);

    String resetPassword(String token, PasswordResetRequest passwordResetRequest);

    TokenResponse refreshToken(String refreshToken);

    void logout(HttpServletRequest request);
}
