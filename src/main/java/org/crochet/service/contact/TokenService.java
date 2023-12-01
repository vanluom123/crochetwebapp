package org.crochet.service.contact;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String createToken(Authentication auth);

    String getUserIdFromToken(String token);

    boolean validateToken(String authToken);
}
