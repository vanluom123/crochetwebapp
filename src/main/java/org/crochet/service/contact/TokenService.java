package org.crochet.service.contact;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.crochet.model.Token;
import org.crochet.model.User;
import org.crochet.payload.response.TokenResponse;

import java.util.Optional;

public interface TokenService {
  Optional<Token> getByToken(String token);

  TokenResponse createToken(User user);

  TokenResponse refreshToken(HttpServletRequest request, HttpServletResponse response);

  void logout(
      HttpServletRequest request,
      HttpServletResponse response
  );
}
