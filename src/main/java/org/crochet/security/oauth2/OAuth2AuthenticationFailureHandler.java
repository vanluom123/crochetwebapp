package org.crochet.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.crochet.util.CookieUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * OAuth2AuthenticationFailureHandler class
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private final OAuth2CookieRepository OAuth2CookieRepository;

  /**
   * Constructor of OAuth2AuthenticationFailureHandler class
   *
   * @param OAuth2CookieRepository HttpCookieOAuth2AuthorizationRequestRepository
   */
  public OAuth2AuthenticationFailureHandler(OAuth2CookieRepository OAuth2CookieRepository) {
    this.OAuth2CookieRepository = OAuth2CookieRepository;
  }

  /**
   * Handle authentication failure
   *
   * @param request the request during which the authentication attempt occurred.
   * @param response the response.
   * @param exception the exception which was thrown to reject the authentication
   * request.
   * @throws IOException I/O exception
   * @throws ServletException a servlet exception
   */
  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    String targetUrl = CookieUtils.getCookie(request, OAuth2CookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue)
        .orElse(("/"));

    targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("error", exception.getLocalizedMessage())
        .build().toUriString();

    OAuth2CookieRepository.removeAuthorizationRequestCookies(request, response);

    getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }
}
