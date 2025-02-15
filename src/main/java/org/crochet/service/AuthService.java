package org.crochet.service;

import jakarta.servlet.http.HttpServletRequest;
import org.crochet.payload.request.LoginRequest;
import org.crochet.payload.request.PasswordResetRequest;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.response.AuthResponse;
import org.crochet.payload.response.TokenResponse;

public interface AuthService {
    AuthResponse authenticateUser(LoginRequest loginRequest);

    void registerUser(SignUpRequest signUpRequest);

    void resendVerificationEmail(String email);

    void confirmToken(String token);

    String resetPasswordLink(String email);

    void resetPassword(String token, PasswordResetRequest passwordResetRequest);

    TokenResponse refreshToken(String refreshToken);

    void logout(HttpServletRequest request);

    AuthResponse getUserInfo(String token);
}
