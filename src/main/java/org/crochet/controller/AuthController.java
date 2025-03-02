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
import org.crochet.payload.response.ResponseData;
import org.crochet.payload.response.TokenResponse;
import org.crochet.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register user")
    @ApiResponse(responseCode = "201",
            description = "Registration successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/signup")
    public ResponseData<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        var response = authService.registerUser(signUpRequest);
        return ResponseData.<String>builder()
                .success(true)
                .code(201)
                .message("Success")
                .data(response)
                .build();
    }

    @Operation(summary = "Confirm user registration")
    @ApiResponse(responseCode = "200",
            description = "Confirmation successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(authService.confirmToken(token));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Resend email verification")
    @ApiResponse(responseCode = "200",
            description = "Email verification resent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/resendVerificationEmail")
    public ResponseData<String> resendVerificationEmail(@RequestParam("email") String email) {
        String response = authService.resendVerificationEmail(email);
        return ResponseData.<String>builder()
                .success(true)
                .code(200)
                .message("Success")
                .data(response)
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Request password reset link")
    @ApiResponse(responseCode = "200",
            description = "Password reset link sent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/password-reset-request")
    public ResponseData<String> resetPasswordRequest(@RequestParam("email") String email) {
        var response = authService.resetPasswordLink(email);
        return ResponseData.<String>builder()
                .success(true)
                .code(200)
                .message("Success")
                .data(response)
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Reset password")
    @ApiResponse(responseCode = "200",
            description = "Password reset successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @PostMapping("/reset-password")
    public ResponseData<String> resetPassword(@RequestParam("passwordResetToken") String passwordResetToken,
                                              @RequestBody PasswordResetRequest passwordResetRequest) {
        var response = authService.resetPassword(passwordResetToken, passwordResetRequest);
        return ResponseData.<String>builder()
                .success(true)
                .code(200)
                .message("Success")
                .data(response)
                .build();
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

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Logout")
    @ApiResponse(responseCode = "200",
            description = "Logout successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/logout")
    public ResponseData<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseData.<String>builder()
                .success(true)
                .code(200)
                .message("Success")
                .data("Logged out successfully")
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get refresh token expired")
    @ApiResponse(responseCode = "200",
            description = "Get refresh token expired",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("refresh-token/expired")
    public ResponseData<LocalDateTime> refreshTokenExpired(@RequestParam("refreshToken") String refreshToken) {
        var response = authService.getRefreshTokenExpiresAt(refreshToken);
        return ResponseData.<LocalDateTime>builder()
                .success(true)
                .code(200)
                .message("Success")
                .data(response)
                .build();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public AuthResponse getUserInfo(@RequestParam("accessToken") String accessToken) {
        return authService.getUserInfo(accessToken);
    }
}
