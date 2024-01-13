package org.crochet.service.contact;

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

  String generateToken(UserDetails userDetails);

  String generateToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails
  );

  String generateRefreshToken(
      UserDetails userDetails
  );

  @Deprecated
  String getUserIdFromToken(String token);

  @Deprecated
  boolean validateToken(String authToken);
}
