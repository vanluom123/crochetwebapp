package org.crochet.security.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.crochet.properties.AppProperties;
import org.crochet.exception.BadRequestException;
import org.crochet.service.contact.JwtTokenService;
import org.crochet.util.CookieUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static org.crochet.security.oauth2.OAuth2CookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * OAuth2AuthenticationSuccessHandler class
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenService jwtTokenService;

    private final AppProperties appProperties;

    private final OAuth2CookieRepository OAuth2CookieRepository;

    /**
     * Constructor of OAuth2AuthenticationSuccessHandler class
     *
     * @param jwtTokenService        TokenProvider
     * @param appProperties          AppProperties
     * @param OAuth2CookieRepository HttpCookieOAuth2AuthorizationRequestRepository
     */
    OAuth2AuthenticationSuccessHandler(JwtTokenService jwtTokenService,
                                       AppProperties appProperties,
                                       OAuth2CookieRepository OAuth2CookieRepository) {
        this.jwtTokenService = jwtTokenService;
        this.appProperties = appProperties;
        this.OAuth2CookieRepository = OAuth2CookieRepository;
    }

    /**
     * Handle authentication success
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     * @throws IOException I/O exception
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Determine target url
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during the authentication process.
     * @return url string
     * @throws BadRequestException Can't proceed with the authentication
     */
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .filter(this::isAuthorizedRedirectUri)
                .orElseThrow(() -> new BadRequestException(
                        "Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication"));

        String targetUrl = redirectUri.isEmpty() ? getDefaultTargetUrl() : redirectUri;
        String token = jwtTokenService.createToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    /**
     * Clear authentication attributes
     *
     * @param request  the request which caused the successful authentication
     * @param response the response
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        OAuth2CookieRepository.removeAuthorizationRequestCookies(request, response);
    }

    /**
     * Authorized redirect uri
     *
     * @param uri uri string
     * @return true or false
     */
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
