package org.crochet.controller;

import org.crochet.request.LoginRequest;
import org.crochet.request.SignUpRequest;
import org.crochet.response.ApiResponse;
import org.crochet.response.AuthResponse;
import org.crochet.service.abstraction.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    AuthResponse authResponse = authService.authenticateUser(loginRequest);
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    ApiResponse response = authService.registerUser(signUpRequest);
    return ResponseEntity.ok(response);
  }

  @GetMapping(path = "/confirm")
  public ResponseEntity<ApiResponse> confirm(@RequestParam("token") String token) {
    return ResponseEntity.ok(authService.confirmToken(token));
  }

  @GetMapping("/resendVerificationEmail")
  public ResponseEntity<ApiResponse> resendVerificationEmail(@RequestParam("email") String email) {
    ApiResponse response = authService.resendVerificationEmail(email);
    return ResponseEntity.ok(response);
  }
}
