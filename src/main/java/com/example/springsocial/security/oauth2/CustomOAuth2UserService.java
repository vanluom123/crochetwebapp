package com.example.springsocial.security.oauth2;

import com.example.springsocial.exception.OAuth2AuthenticationProcessingException;
import com.example.springsocial.model.AuthProvider;
import com.example.springsocial.model.User;
import com.example.springsocial.repository.UserRepository;
import com.example.springsocial.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Autowired
  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
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

    // Validate the user's provider
    validateUserProvider(oAuth2UserRequest, user);

    // Create and return the user principal
    return UserPrincipal.create(user, oAuth2User.getAttributes());
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
   * Validates the user's provider against the OAuth2 user request.
   *
   * @param oAuth2UserRequest the OAuth2 user request
   * @param user              the user to validate
   * @throws OAuth2AuthenticationProcessingException if the user's provider does not match the OAuth2 client registration
   */
  private void validateUserProvider(OAuth2UserRequest oAuth2UserRequest, User user) {
    AuthProvider provider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId());
    if (!user.getProvider().equals(provider)) {
      throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
          user.getProvider() + " account. Please use your " + user.getProvider() +
          " account to login.");
    }
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
    String imageUrl = oAuth2User.getAttribute("imageUrl");

    // Create a new user
    User user = new User();
    user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
    user.setProviderId(providerId);
    user.setName(name);
    user.setEmail(email);
    user.setImageUrl(imageUrl);

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
    String imageUrl = oAuth2User.getAttribute("imageUrl");

    // Update the existing user's attributes
    existingUser.setName(name);
    existingUser.setImageUrl(imageUrl);

    // Save the updated user in the repository
    return userRepository.save(existingUser);
  }


}
