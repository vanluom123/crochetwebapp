package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.crochet.request.LoginRequest;
import org.crochet.request.PasswordResetRequest;
import org.crochet.request.SignUpRequest;
import org.crochet.response.AuthResponse;
import org.crochet.response.EntityResponse;
import org.crochet.service.contact.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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
    @ApiResponse(responseCode = "200",
            description = "Registration successful",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
    @PostMapping("/signup")
    public ResponseEntity<EntityResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        EntityResponse response = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Confirm user registration")
    @ApiResponse(responseCode = "200",
            description = "Confirmation successful",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
    @GetMapping(path = "/confirm")
    public ResponseEntity<EntityResponse> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(authService.confirmToken(token));
    }

    @Operation(summary = "Resend email verification")
    @ApiResponse(responseCode = "200",
            description = "Email verification resent successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
    @GetMapping("/resendVerificationEmail")
    public ResponseEntity<EntityResponse> resendVerificationEmail(@RequestParam("email") String email) {
        EntityResponse response = authService.resendVerificationEmail(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Request password reset link")
    @ApiResponse(responseCode = "200",
            description = "Password reset link sent successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
    @GetMapping("/password-reset-request")
    public ResponseEntity<EntityResponse> resetPasswordRequest(@RequestParam("email") String email) {
        var response = authService.resetPasswordLink(email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reset password")
    @ApiResponse(responseCode = "200",
            description = "Password reset successful",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class)))
    @PostMapping("/reset-password")
    public ResponseEntity<EntityResponse> resetPassword(@RequestParam("passwordResetToken") String passwordResetToken,
                                                        @RequestBody PasswordResetRequest passwordResetRequest) {
        var response = authService.resetPassword(passwordResetToken, passwordResetRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout")
    @GetMapping("/logout")
    public String logout(HttpServletResponse response,
                         @CookieValue(name = "jwtToken", required = false) String jwtToken) {
        if (jwtToken != null) {
            Cookie cookie = new Cookie("jwtToken", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "Logged out successfully.";
        } else {
            return "No JWT token found in the cookie.";
        }
    }
}
