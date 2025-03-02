package org.crochet.security.oauth2;

import org.crochet.enumerator.AuthProvider;
import org.crochet.enumerator.RoleType;
import org.crochet.exception.OAuth2AuthenticationProcessingException;
import org.crochet.model.User;
import org.crochet.repository.UserRepository;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * CustomOAuth2UserService class
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * Constructor of CustomOAuth2UserService class
     *
     * @param userRepository UserRepository
     */
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Processes the OAuth2 user, either by updating an existing user or registering a new user.
     *
     * @param oAuth2UserRequest the OAuth2 user request
     * @param oAuth2User        the OAuth2 user
     * @return the authenticated user
     */
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        // Extract email from OAuth2 user
        String email = getEmailFromOAuth2User(oAuth2User);

        // Check if user exists in the repository
        Optional<User> userOptional = getUserByEmail(email);

        // If user exists, update the user; otherwise, register a new user
        User user = userOptional.map(existingUser -> updateUser(existingUser, oAuth2User))
                .orElseGet(() -> registerNewUser(oAuth2UserRequest, oAuth2User));

        user.setAttributes(oAuth2User.getAttributes());

        return user;
    }

    /**
     * Extracts the email from the OAuth2 user.
     *
     * @param oAuth2User the OAuth2 user
     * @return the email address
     * @throws OAuth2AuthenticationProcessingException if email is not found from OAuth2 provider
     */
    private String getEmailFromOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        if (!StringUtils.hasLength(email)) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        return email;
    }

    /**
     * Retrieves a user from the repository based on the email address.
     *
     * @param email the email address
     * @return an optional user
     */
    private Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Registers a new user based on the OAuth2 user.
     *
     * @param oAuth2UserRequest the OAuth2 user request
     * @param oAuth2User        the OAuth2 user
     * @return the registered user
     */
    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String providerId = oAuth2User.getAttribute("id");
        String name = oAuth2User.getAttribute("name");
        String email = getEmailFromOAuth2User(oAuth2User);
        String imageUrl = oAuth2User.getAttribute("picture");
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase();
        AuthProvider provider = AuthProvider.valueOf(registrationId);

        // Create a new user
        User user = User.builder()
                .role(RoleType.USER)
                .provider(provider)
                .providerId(providerId)
                .name(name)
                .email(email)
                .imageUrl(imageUrl)
                .emailVerified(true)
                .build();
        // Save the new user in the repository
        return userRepository.save(user);
    }

    /**
     * Updates an existing user with the attributes from the OAuth2 user.
     *
     * @param existingUser the existing user
     * @param oAuth2User   the OAuth2 user
     * @return the updated user
     */
    private User updateUser(User existingUser, OAuth2User oAuth2User) {
        String name = oAuth2User.getAttribute("name");
        String imageUrl = oAuth2User.getAttribute("picture");

        // Update the existing user's attributes
        existingUser.setName(name);
        existingUser.setImageUrl(imageUrl);

        // Save the updated user in the repository
        return userRepository.save(existingUser);
    }
}
