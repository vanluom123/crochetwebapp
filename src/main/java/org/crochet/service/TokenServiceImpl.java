package org.crochet.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.crochet.config.AppProperties;
import org.crochet.security.UserPrincipal;
import org.crochet.service.contact.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * TokenProvider class
 */
@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final AppProperties appProperties;

    /**
     * Constructs a TokenProvider with the provided AppProperties dependency.
     *
     * @param appProperties The AppProperties dependency.
     */
    public TokenServiceImpl(AppProperties appProperties) {
        this.appProperties = appProperties;
    }


    /**
     * Creates a JWT token for the provided Authentication object.
     *
     * @param auth The Authentication object representing the authenticated user.
     * @return The created JWT token.
     */
    @Override
    public String createToken(Authentication auth) {
        // Get the UserPrincipal from the Authentication object
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();

        // Get the current date and time
        Date now = new Date();

        // Expire date
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMs());

        // Build and sign the JWT token
        return Jwts.builder()
                .setSubject(userPrincipal.getId().toString()) // Set the subject of the token as the user ID
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
    @Override
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
    @Override
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
