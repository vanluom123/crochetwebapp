package com.example.springsocial.security;


import com.example.springsocial.exception.ResourceNotFoundException;
import com.example.springsocial.model.User;
import com.example.springsocial.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Constructs a CustomUserDetailsService with the provided UserRepository dependency.
   *
   * @param userRepository The UserRepository dependency.
   */
  @Autowired
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
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    // Create a UserPrincipal object from the retrieved user
    return UserPrincipal.create(user);
  }

}