package org.crochet.controller;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.User;
import org.crochet.repository.UserRepository;
import org.crochet.security.CurrentUser;
import org.crochet.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

  private final UserRepository userRepository;

  @Autowired
  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/hello")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<String> getHello() {
    return ResponseEntity.ok("Hello World!");
  }

  @GetMapping("/current")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<User> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
    var user = userRepository.findById(userPrincipal.getId())
        .orElseThrow(() -> new ResourceNotFoundException(String.format("User with %s is not found", userPrincipal.getId())));
    return ResponseEntity.ok(user);
  }
}
