package org.crochet.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtTokenService {
    @Deprecated
    String createToken(Authentication auth);

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateToken(String username);

    String generateToken(
            Map<String, Object> extraClaims,
            String username
    );

    @Deprecated
    String getUserIdFromToken(String token);

    boolean validateToken(String authToken);
}
