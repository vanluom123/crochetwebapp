package org.crochet.service.abstraction;

import org.crochet.request.SignUpRequest;
import org.crochet.response.UserResponse;

public interface UserService {
  UserResponse createUser(SignUpRequest signUpRequest);
  void verifyEmail(String email);
}
