package org.crochet.security;

import org.crochet.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * TokenProvider class
 */
@Service
public class TokenProvider {

  private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

  private final AppProperties appProperties;

  /**
   * Constructs a TokenProvider with the provided AppProperties dependency.
   *
   * @param appProperties The AppProperties dependency.
   */
  @Autowired
  public TokenProvider(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  /**
   * Create token
   *
   * @param auth Authentication
   * @return Token string
   */
  public String createToken(Authentication auth) {
    return createToken(auth, false);
  }

  /**
   * Create refresh token
   *
   * @param auth Authentication
   * @return Refresh token string
   */
  public String createRefreshToken(Authentication auth) {
    return createToken(auth, true);
  }

  /**
   * Creates a JWT token for the provided Authentication object.
   *
   * @param auth         The Authentication object representing the authenticated user.
   * @param isRefreshed  A flag indicating whether the token is a refreshed token or not.
   * @return The created JWT token.
   */
  private String createToken(Authentication auth, boolean isRefreshed) {
    // Get the UserPrincipal from the Authentication object
    UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

    // Get the current date and time
    Date now = new Date();

    // Determine the token expiration time based on the isRefreshed flag
    long tokenExpirationMs = isRefreshed ? appProperties.getAuth().getRefreshTokenExpirationMs() : appProperties.getAuth().getTokenExpirationMs();
    Date expiryDate = new Date(now.getTime() + tokenExpirationMs);

    // Build and sign the JWT token
    return Jwts.builder()
        .setSubject(Long.toString(userPrincipal.getId())) // Set the subject of the token as the user ID
        .setIssuedAt(new Date()) // Set the issued date as the current date
        .setExpiration(expiryDate) // Set the token expiration date
        .signWith(getKey(), SignatureAlgorithm.HS512) // Sign the token using the key and algorithm
        .compact(); // Compact the token into its final string representation
  }


  /**
   * Retrieves the cryptographic key used for token generation and validation.
   *
   * @return The cryptographic key.
   */
  private Key getKey() {
    // Decode the Base64-encoded token secret from the application properties
    byte[] keyBytes = Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret());

    // Create and return an HMAC-based cryptographic key using the decoded key bytes
    return Keys.hmacShaKeyFor(keyBytes);
  }


  /**
   * Extracts the user ID from the provided token.
   *
   * @param token The token from which to extract the user ID.
   * @return The user ID extracted from the token.
   */
  public Long getUserIdFromToken(String token) {
    // Parse the token, validate its signature, and retrieve the claims
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    // Extract and return the user ID from the claims
    return Long.parseLong(claims.getSubject());
  }


  /**
   * Validates the authenticity and integrity of the provided JWT token.
   *
   * @param authToken The JWT token to validate.
   * @return true if the token is valid, false otherwise.
   */
  public boolean validateToken(String authToken) {
    try {
      // Parse the token and validate it using the configured signing key
      Jwts.parserBuilder()
          .setSigningKey(getKey())
          .build()
          .parseClaimsJws(authToken);
      // If no exception is thrown during parsing and validation, the token is considered valid
      return true;
    } catch (SecurityException ex) {
      logger.error("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      logger.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      logger.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      logger.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty.");
    }
    // If an exception is caught during parsing or validation, the token is considered invalid
    return false;
  }


}
