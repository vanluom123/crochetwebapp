package org.crochet.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class TokenUtils {
    /**
     * Retrieves the JWT (JSON Web Token) from the "Authorization" header of an HTTP request.
     *
     * @param request The HttpServletRequest representing the HTTP request.
     * @return The extracted JWT, or null if it is not found.
     */
    public static String getJwtFromAuthorizationHeader(HttpServletRequest request) {
        // Retrieve the value of the "Authorization" header from the request
        String bearerToken = request.getHeader("Authorization");

        // Check if the "Authorization" header value is not empty and starts with "Bearer "
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Extract and return the JWT by removing the "Bearer " prefix
            return bearerToken.substring(7);
        }

        // Return null if the JWT is not found or the "Authorization" header is missing or malformed
        return null;
    }
}
