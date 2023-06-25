package com.example.springsocial.controller;

import com.example.springsocial.payload.ApiResponse;
import com.example.springsocial.payload.AuthResponse;
import com.example.springsocial.payload.LoginRequest;
import com.example.springsocial.payload.RegisterResponse;
import com.example.springsocial.payload.SignUpRequest;
import com.example.springsocial.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    RegisterResponse response = authService.registerUser(signUpRequest);
    return ResponseEntity.created(response.getLocation())
        .body(response.getApiResponse());
  }

}
