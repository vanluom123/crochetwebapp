package org.crochet.security;


import org.crochet.model.User;
import org.crochet.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
                .filter(User::isEmailVerified)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Create a list of GrantedAuthority with a single authority ROLE_USER
        List<GrantedAuthority> authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // Create a UserPrincipal object from the retrieved user
        var userDetails = UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .attributes(Map.of("name", email))
                .build();

        return userDetails;
    }
}