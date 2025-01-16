package org.crochet.security;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.User;
import org.crochet.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.MSG_EMAIL_NOT_VERIFIED;
import static org.crochet.constant.MessageConstant.MSG_USER_LOGIN_REQUIRED;

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
                .orElseThrow(() -> new ResourceNotFoundException(MSG_USER_LOGIN_REQUIRED,
                        MAP_CODE.get(MSG_USER_LOGIN_REQUIRED)));

        // Check if the user is email verified
        if (!user.isEmailVerified()) {
            throw new ResourceNotFoundException(MSG_EMAIL_NOT_VERIFIED,
                    MAP_CODE.get(MSG_EMAIL_NOT_VERIFIED));
        }

        return user;
    }
}