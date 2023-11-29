package org.crochet.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.crochet.request.LoginRequest;
import org.crochet.request.PasswordResetRequest;
import org.crochet.request.SignUpRequest;
import org.crochet.response.ApiResponse;
import org.crochet.response.AuthResponse;
import org.crochet.service.AuthService;
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

    @GetMapping("/password-reset-request")
    public ResponseEntity<ApiResponse> resetPasswordRequest(@RequestParam("email") String email) {
        var response = authService.resetPasswordLink(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam("passwordResetToken") String passwordResetToken,
                                                     @RequestBody PasswordResetRequest passwordResetRequest) {
        var response = authService.resetPassword(passwordResetToken, passwordResetRequest);
        return ResponseEntity.ok(response);
    }

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
