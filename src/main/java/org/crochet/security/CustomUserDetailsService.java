package org.crochet.security;


import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.User;
import org.crochet.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Retrieve the user by email from the UserRepository
        User user = userRepository.findByEmail(email)
                .filter(User::getEmailVerified)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Create a UserPrincipal object from the retrieved user
        return UserPrincipal.create(user);
    }


    /**
     * Loads user details based on the provided user ID.
     *
     * @param id The ID of the user.
     * @return The UserDetails object representing the user.
     * @throws ResourceNotFoundException If the user is not found for the given ID.
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        // Retrieve the user by ID from the UserRepository
        User user = userRepository.findById(id)
                .filter(User::getEmailVerified)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with %s not found", id)));

        // Create a UserPrincipal object from the retrieved user
        return UserPrincipal.create(user);
    }

}