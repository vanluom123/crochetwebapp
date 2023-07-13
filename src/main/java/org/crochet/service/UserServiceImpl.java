package org.crochet.service;

import org.crochet.exception.BadRequestException;
import org.crochet.mapper.UserMapper;
import org.crochet.model.AuthProvider;
import org.crochet.model.User;
import org.crochet.repository.UserRepository;
import org.crochet.request.SignUpRequest;
import org.crochet.response.UserResponse;
import org.crochet.service.abstraction.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserServiceImpl(UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public UserResponse createUser(SignUpRequest signUpRequest) {
    // Check if the email address is already in use
    boolean isPresent = userRepository.findByEmail(signUpRequest.getEmail()).isPresent();

    if (isPresent) {
      throw new BadRequestException("Email address already in use");
    }

    // Creating user's account
    User user = new User()
        .setName(signUpRequest.getName())
        .setEmail(signUpRequest.getEmail())
        .setPassword(passwordEncoder.encode(signUpRequest.getPassword()))
        .setProvider(AuthProvider.local);

    // Save the user to the repository
    user = userRepository.save(user);

    return UserMapper.INSTANCE.toUserResponse(user);
  }

  @Override
  public void verifyEmail(String email) {
    userRepository.verifyEmail(email);
  }
}
