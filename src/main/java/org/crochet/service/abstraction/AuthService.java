package org.crochet.service.abstraction;

import org.crochet.response.ApiResponse;
import org.crochet.response.AuthResponse;
import org.crochet.request.LoginRequest;
import org.crochet.request.SignUpRequest;

public interface AuthService {
  AuthResponse authenticateUser(LoginRequest loginRequest);
  ApiResponse registerUser(SignUpRequest signUpRequest);
  ApiResponse resendVerificationEmail(String email);
  ApiResponse confirmToken(String token);
}
