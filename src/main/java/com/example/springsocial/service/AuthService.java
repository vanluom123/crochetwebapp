package com.example.springsocial.service;

import com.example.springsocial.payload.AuthResponse;
import com.example.springsocial.payload.LoginRequest;
import com.example.springsocial.payload.RegisterResponse;
import com.example.springsocial.payload.SignUpRequest;

public interface AuthService {
  public AuthResponse authenticateUser(LoginRequest loginRequest);
  public RegisterResponse registerUser(SignUpRequest signUpRequest);
}
