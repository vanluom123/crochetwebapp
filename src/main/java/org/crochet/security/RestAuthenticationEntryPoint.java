package org.crochet.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * RestAuthenticationEntryPoint class
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private static final Logger logger = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

  /**
   * Commence
   *
   * @param httpServletRequest that resulted in an <code>AuthenticationException</code>
   * @param httpServletResponse so that the user agent can begin authentication
   * @param e that caused the invocation
   * @throws IOException I/O exception
   */
  @Override
  public void commence(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AuthenticationException e) throws IOException {
    logger.error("Responding with unauthorized error. Message - {}", e.getMessage());
    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
        e.getLocalizedMessage());
  }
}
