package org.crochet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.crochet.service.contact.JwtTokenService;
import org.crochet.service.contact.TokenService;
import org.crochet.util.TokenUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TokenAuthenticationFilter class
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);
  private final JwtTokenService jwtTokenService;
  private final CustomUserDetailsService customUserDetailsService;
  private final TokenService tokenService;

  public TokenAuthenticationFilter(JwtTokenService jwtTokenService,
                                   CustomUserDetailsService customUserDetailsService,
                                   TokenService tokenService) {
    this.jwtTokenService = jwtTokenService;
    this.customUserDetailsService = customUserDetailsService;
    this.tokenService = tokenService;
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
  protected void doFilterInternal(@NotNull HttpServletRequest request,
                                  @NotNull HttpServletResponse response,
                                  @NotNull FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Get jwtToken
      var jwtToken = TokenUtils.getJwtFromAuthorizationHeader(request);
      String userEmail = jwtTokenService.extractUsername(jwtToken);

      // Check if the JWT exists and is valid
      if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        // Load the user details by email
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
        var isTokenValid = tokenService.getByToken(jwtToken)
            .map(t -> !t.isExpired() && !t.isRevoked())
            .orElse(false);
        if (jwtTokenService.isTokenValid(jwtToken, userDetails) && isTokenValid) {
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
