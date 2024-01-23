package org.crochet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.crochet.service.contact.JwtTokenService;
import org.crochet.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.util.StringUtils.hasText;

/**
 * TokenAuthenticationFilter class
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);
    private final JwtTokenService jwtTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    public TokenAuthenticationFilter(JwtTokenService jwtTokenService,
                                     CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Performs the filtering logic for the authentication process.
     *
     * @param request     The HttpServletRequest object.
     * @param response    The HttpServletResponse object.
     * @param filterChain The FilterChain object for invoking the next filter in the chain.
     * @throws ServletException If an exception occurs during the filtering process.
     * @throws IOException      If an I/O exception occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Get jwtToken
            var jwtToken = TokenUtils.getJwtFromAuthorizationHeader(request);

            // Check if the JWT exists and is valid
            if (hasText(jwtToken) && jwtTokenService.validateToken(jwtToken)) {
                String username = jwtTokenService.extractUsername(jwtToken);

                // Load the user details by email
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                if (jwtTokenService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
