package org.crochet.service.impl;

import org.crochet.properties.MessageCodeProperties;
import org.crochet.constant.MessageConstant;
import org.crochet.enumerator.AuthProvider;
import org.crochet.exception.BadRequestException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.User;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.repository.UserRepository;
import org.crochet.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.crochet.constant.MessageConstant.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageCodeProperties msgCodeProps;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           MessageCodeProperties msgCodeProps) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.msgCodeProps = msgCodeProps;
    }

    @Override
    public User createUser(SignUpRequest signUpRequest) {
        // Check if the email address is already in use
        if (isValidEmail(signUpRequest.getEmail())) {
            throw new BadRequestException(MessageConstant.EMAIL_ADDRESS_ALREADY_IN_USE_MESSAGE,
                    msgCodeProps.getCode("EMAIL_ADDRESS_ALREADY_IN_USE_MESSAGE"));
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
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_EMAIL_MESSAGE + email,
                        msgCodeProps.getCode("USER_NOT_FOUND_WITH_EMAIL_MESSAGE")));
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID_MESSAGE + id,
                        msgCodeProps.getCode("USER_NOT_FOUND_WITH_ID_MESSAGE")));
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
            throw new BadRequestException(INCORRECT_PASSWORD_MESSAGE,
                    msgCodeProps.getCode("INCORRECT_PASSWORD_MESSAGE"));
        }
        return user;
    }

    private boolean isValidEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
