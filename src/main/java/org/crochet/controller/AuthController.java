package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.crochet.payload.request.LoginRequest;
import org.crochet.payload.request.PasswordResetRequest;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.response.AuthResponse;
import org.crochet.service.contact.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @Operation(summary = "Authenticate user")
  @ApiResponse(responseCode = "200",
      description = "Authentication successful",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = AuthResponse.class)))
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    AuthResponse authResponse = authService.authenticateUser(loginRequest);
    return ResponseEntity.ok(authResponse);
  }

  @Operation(summary = "Register user")
  @ApiResponse(responseCode = "201",
      description = "Registration successful",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiResponse.class)))
  @PostMapping("/signup")
  public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
    String response = authService.registerUser(signUpRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(response);
  }

  @Operation(summary = "Confirm user registration")
  @ApiResponse(responseCode = "200",
      description = "Confirmation successful",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiResponse.class)))
  @GetMapping(path = "/confirm")
  public ResponseEntity<String> confirm(@RequestParam("token") String token) {
    return ResponseEntity.ok(authService.confirmToken(token));
  }

  @Operation(summary = "Resend email verification")
  @ApiResponse(responseCode = "200",
      description = "Email verification resent successfully",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiResponse.class)))
  @GetMapping("/resendVerificationEmail")
  public ResponseEntity<String> resendVerificationEmail(@RequestParam("email") String email) {
    String response = authService.resendVerificationEmail(email);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Request password reset link")
  @ApiResponse(responseCode = "200",
      description = "Password reset link sent successfully",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = ApiResponse.class)))
  @GetMapping("/password-reset-request")
  public ResponseEntity<String> resetPasswordRequest(@RequestParam("email") String email) {
    var response = authService.resetPasswordLink(email);
    return ResponseEntity.ok(response);
  }

    @Operation(summary = "Reset password")
    @ApiResponse(responseCode = "200",
            description = "Password reset successful",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("passwordResetToken") String passwordResetToken,
                                                        @RequestBody PasswordResetRequest passwordResetRequest) {
        var response = authService.resetPassword(passwordResetToken, passwordResetRequest);
        return ResponseEntity.ok(response);
    }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    authService.refreshToken(request, response);
  }

  @GetMapping("/logout")
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    authService.logout(request, response);
  }
}
