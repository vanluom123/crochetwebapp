package org.crochet.security.oauth2.user;

import org.crochet.exception.OAuth2AuthenticationProcessingException;
import org.crochet.enumerator.AuthProvider;

import java.util.Map;

public class OAuth2UserFactory {

    public static AbstractOAuth2User getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.getValue())) {
            return new GoogleOAuth2User(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.FACEBOOK.getValue())) {
            return new FacebookOAuth2User(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.getValue())) {
            return new GithubOAuth2User(attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}
