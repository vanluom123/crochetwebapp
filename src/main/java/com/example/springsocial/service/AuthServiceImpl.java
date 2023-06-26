package com.example.springsocial.service;

import com.example.springsocial.exception.BadRequestException;
import com.example.springsocial.model.AuthProvider;
import com.example.springsocial.model.User;
import com.example.springsocial.payload.ApiResponse;
import com.example.springsocial.payload.AuthResponse;
import com.example.springsocial.payload.LoginRequest;
import com.example.springsocial.payload.RegisterResponse;
import com.example.springsocial.payload.SignUpRequest;
import com.example.springsocial.repository.UserRepository;
import com.example.springsocial.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Service
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  private final TokenProvider tokenProvider;

  @Autowired
  public AuthServiceImpl(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         TokenProvider tokenProvider) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenProvider = tokenProvider;
  }

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * @param loginRequest The login request containing the user's email and password.
   * @return An AuthResponse containing the authentication token.
   */
  @Override
  public AuthResponse authenticateUser(LoginRequest loginRequest) {
    // Create an authentication token with the provided email and password
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
    // Authenticate the token using the authentication manager
    Authentication authentication = authenticationManager.authenticate(authToken);

    // Set the authenticated authentication object in the security context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Create a token for the authenticated user
    String token = tokenProvider.createToken(authentication);

    // Return the authentication token in an AuthResponse
    return new AuthResponse(token);
  }

  /**
   * Registers a new user based on the provided sign-up request.
   *
   * @param signUpRequest The sign-up request containing user information.
   * @return The response indicating the success of user registration.
   * @throws BadRequestException If the email address is already in use.
   */
  @Override
  @Transactional
  public RegisterResponse registerUser(SignUpRequest signUpRequest) {
    // Check if the email address is already in use
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new BadRequestException("Email address already in use.");
    }

    // Creating user's account
    User user = new User()
        .setName(signUpRequest.getName())
        .setEmail(signUpRequest.getEmail())
        .setPassword(passwordEncoder.encode(signUpRequest.getPassword()))
        .setProvider(AuthProvider.local);

    // Save the user to the repository
    user = userRepository.save(user);

    // Build the URI for the newly registered user
    URI location = ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/user/me")
        .buildAndExpand(user.getId())
        .toUri();

    // Create the RegisterResponse with the location and a success API response
    return new RegisterResponse()
        .setLocation(location)
        .setApiResponse(new ApiResponse(true, "User registered successfully@"));
  }

}
