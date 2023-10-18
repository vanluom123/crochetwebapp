package org.crochet.service.abstraction;

import org.springframework.security.core.Authentication;

public interface TokenService {
  String createToken(Authentication auth);

  Long getUserIdFromToken(String token);

  boolean validateToken(String authToken);
}
