package org.crochet.util;

import org.crochet.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Gets the currently authenticated user from the security context
     *
     * @return The authenticated User object, or null if no user is authenticated
     */
    public static User getCurrentUser() {
        Authentication authentication = getAuthentication();
        if (!isValidAuthentication(authentication)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static boolean isValidAuthentication(Authentication authentication) {
        return authentication != null 
               && authentication.isAuthenticated()
               && !authentication.getPrincipal().equals("anonymousUser");
    }
}
