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

  @Override
  public AuthResponse authenticateUser(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(),
            loginRequest.getPassword()
        )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = tokenProvider.createToken(authentication);
    return new AuthResponse(token);
  }

  @Override
  @Transactional
  public RegisterResponse registerUser(SignUpRequest signUpRequest) {
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new BadRequestException("Email address already in use.");
    }

    // Creating user's account
    User user = new User()
        .setName(signUpRequest.getName())
        .setEmail(signUpRequest.getEmail())
        .setPassword(passwordEncoder.encode(signUpRequest.getPassword()))
        .setProvider(AuthProvider.local);

    user = userRepository.save(user);

    URI location = ServletUriComponentsBuilder
        .fromCurrentContextPath().path("/user/me")
        .buildAndExpand(user.getId()).toUri();

    return new RegisterResponse()
        .setLocation(location)
        .setApiResponse(new ApiResponse(true, "User registered successfully@"));
  }
}
