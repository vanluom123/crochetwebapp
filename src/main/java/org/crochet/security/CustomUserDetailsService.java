package org.crochet.security;


import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.User;
import org.crochet.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.MSG_EMAIL_NOT_VERIFIED;
import static org.crochet.constant.MessageConstant.MSG_USER_LOGIN_REQUIRED;

import java.util.Collections;
import java.util.List;

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
     * Loads user details based on the provided email.
     *
     * @param email The email of the user.
     * @return The UserDetails object representing the user.
     * @throws UsernameNotFoundException If the user is not found for the given email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Retrieve the user by email from the UserRepository
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_USER_LOGIN_REQUIRED,
                        MAP_CODE.get(MSG_USER_LOGIN_REQUIRED)));

        // Check if the user is email verified
        if (!user.isEmailVerified()) {
            throw new ResourceNotFoundException(MSG_EMAIL_NOT_VERIFIED,
                    MAP_CODE.get(MSG_EMAIL_NOT_VERIFIED));
        }

        // Create a list of GrantedAuthority with a single authority ROLE_USER
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // Create a UserPrincipal object from the retrieved user

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}