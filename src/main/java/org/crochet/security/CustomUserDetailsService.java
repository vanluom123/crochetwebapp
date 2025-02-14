package org.crochet.security;

import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.User;
import org.crochet.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService class
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a CustomUserDetailsService with the provided UserRepository dependency.
     *
     * @param userRepository The UserRepository dependency.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load the user by ID from the UserRepository.
     *
     * @param username The ID of the user.
     * @return The user details.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the user by ID from the UserRepository
        User user = userRepository.findById(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResultCode.MSG_USER_LOGIN_REQUIRED.message(),
                        ResultCode.MSG_USER_LOGIN_REQUIRED.code()));

        // Check if the user is email verified
        if (!user.isEmailVerified()) {
            throw new ResourceNotFoundException(ResultCode.MSG_EMAIL_NOT_VERIFIED.message(),
                    ResultCode.MSG_EMAIL_NOT_VERIFIED.code());
        }

        return user;
    }
}