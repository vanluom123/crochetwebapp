package org.crochet.service.abstraction;

import org.crochet.request.LoginRequest;
import org.crochet.request.PasswordResetRequest;
import org.crochet.request.SignUpRequest;
import org.crochet.response.ApiResponse;
import org.crochet.response.AuthResponse;

public interface AuthService {
  AuthResponse authenticateUser(LoginRequest loginRequest);
  ApiResponse registerUser(SignUpRequest signUpRequest);
  ApiResponse resendVerificationEmail(String email);
  ApiResponse confirmToken(String token);

  ApiResponse resetPasswordLink(String email);

  ApiResponse resetPassword(String token, PasswordResetRequest passwordResetRequest);
}
