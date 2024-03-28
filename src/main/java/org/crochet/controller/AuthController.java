package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.crochet.payload.request.LoginRequest;
import org.crochet.payload.request.PasswordResetRequest;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.response.AuthResponse;
import org.crochet.payload.response.TokenResponse;
import org.crochet.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Register user")
    @ApiResponse(responseCode = "201",
            description = "Registration successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        String response = authService.registerUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Confirm user registration")
    @ApiResponse(responseCode = "200",
            description = "Confirmation successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(authService.confirmToken(token));
    }

    @Operation(summary = "Resend email verification")
    @ApiResponse(responseCode = "200",
            description = "Email verification resent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/resendVerificationEmail")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam("email") String email) {
        String response = authService.resendVerificationEmail(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Request password reset link")
    @ApiResponse(responseCode = "200",
            description = "Password reset link sent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/password-reset-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam("email") String email) {
        var response = authService.resetPasswordLink(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reset password")
    @ApiResponse(responseCode = "200",
            description = "Password reset successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("passwordResetToken") String passwordResetToken,
                                                @RequestBody PasswordResetRequest passwordResetRequest) {
        var response = authService.resetPassword(passwordResetToken, passwordResetRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh access token")
    @ApiResponse(responseCode = "200",
            description = "Access token refreshed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class)))
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        var tokenResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "Logout")
    @ApiResponse(responseCode = "200",
            description = "Logout successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("Logout successful");
    }
}
