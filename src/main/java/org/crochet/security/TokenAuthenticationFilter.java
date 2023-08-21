package org.crochet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.crochet.util.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TokenAuthenticationFilter class
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

  /**
   * Performs the filtering logic for the authentication process.
   *
   * @param request      The HttpServletRequest object.
   * @param response     The HttpServletResponse object.
   * @param filterChain  The FilterChain object for invoking the next filter in the chain.
   * @throws ServletException If an exception occurs during the filtering process.
   * @throws IOException      If an I/O exception occurs.
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      // Get jwtToken from header
      String jwtToken = getJwtFromRequest(request);

      // Get jwtToken from cookie
      var tokenFromCookie = CookieUtils.getCookie(request, "jwtToken").map(Cookie::getValue).orElse(null);

      // Check if the JWT exists and is valid
      if (StringUtils.hasText(jwtToken) && tokenProvider.validateToken(jwtToken, tokenFromCookie)) {
        Long userId = tokenProvider.getUserIdFromToken(tokenFromCookie);

        // Load the user details by user ID
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);

        // Create an authentication token with the user details and set the authentication details
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception ex) {
      logger.error("Could not set user authentication in security context", ex);
    }

    // Continue the filter chain
    filterChain.doFilter(request, response);
  }


  /**
   * Retrieves the JWT (JSON Web Token) from the "Authorization" header of an HTTP request.
   *
   * @param request The HttpServletRequest representing the HTTP request.
   * @return The extracted JWT, or null if it is not found.
   */
  private String getJwtFromRequest(HttpServletRequest request) {
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
