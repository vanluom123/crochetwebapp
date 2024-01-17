package org.crochet.service;

import org.crochet.enumerator.AuthProvider;
import org.crochet.exception.BadRequestException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.User;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.repository.UserRepository;
import org.crochet.service.contact.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(SignUpRequest signUpRequest) {
        // Check if the email address is already in use
        if (isValidEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use");
        }

        // Creating user's account
        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .emailVerified(false)
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(AuthProvider.LOCAL)
                .role(signUpRequest.getRole())
                .build();
        // Save the user to the repository
        return userRepository.save(user);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not existed with email " + email));
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not existed with id " + id));
    }

    @Override
    public void updatePassword(String password, String email) {
        userRepository.updatePassword(password, email);
    }

    @Override
    public void verifyEmail(String email) {
        userRepository.verifyEmail(email);
    }

    @Override
    public User checkLogin(String email, String password) {
        var user = this.getByEmail(email);
        var isMatch = passwordEncoder.matches(password, user.getPassword());
        if (!isMatch) {
            throw new BadRequestException("Incorrect password");
        }
        return user;
    }

    private boolean isValidEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
