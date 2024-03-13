package org.crochet.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.crochet.properties.AppProperties;
import org.crochet.security.UserPrincipal;
import org.crochet.service.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * TokenProvider class
 */
@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenServiceImpl.class);

    private final AppProperties appProperties;

    /**
     * Constructs a TokenProvider with the provided AppProperties dependency.
     *
     * @param appProperties The AppProperties dependency.
     */
    public JwtTokenServiceImpl(AppProperties appProperties) {
        this.appProperties = appProperties;
    }


    /**
     * Creates a JWT token for the provided Authentication object.
     *
     * @param auth The Authentication object representing the authenticated user.
     * @return The created JWT token.
     */
    @Deprecated
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

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username);
    }

    @Override
    public String generateToken(
            Map<String, Object> extraClaims,
            String username
    ) {
        return buildToken(extraClaims, username, appProperties.getAuth().getTokenExpirationMs());
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String username,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
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
    @Deprecated
    @Override
    public String getUserIdFromToken(String token) {
        // Parse the token, validate its signature, and retrieve the claims
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Extract and return the user ID from the claims
        return claims.getSubject();
    }


    /**
     * Validates the authenticity and integrity of the provided JWT token.
     *
     * @param authToken The JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    @Deprecated
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
