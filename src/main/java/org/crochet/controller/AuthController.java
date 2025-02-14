package org.crochet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.crochet.enums.ResultCode;
import org.crochet.payload.request.LoginRequest;
import org.crochet.payload.request.PasswordResetRequest;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.response.AuthResponse;
import org.crochet.payload.response.ResponseData;
import org.crochet.payload.response.TokenResponse;
import org.crochet.service.AuthService;
import org.crochet.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Authenticate user")
    @ApiResponse(responseCode = "200",
            description = "Authentication successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ResponseData<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseUtil.success(authResponse);
    }

    @Operation(summary = "Register user")
    @ApiResponse(responseCode = "201",
            description = "Registration successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ResponseData<String> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        signUpRequest.setEmail(signUpRequest.getEmail().toLowerCase());
        authService.registerUser(signUpRequest);
        return ResponseUtil.success(HttpStatus.CREATED, ResultCode.MSG_USER_REGISTER_SUCCESS.message());
    }

    @Operation(summary = "Confirm user registration")
    @ApiResponse(responseCode = "200",
            description = "Confirmation successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/confirm")
    public ResponseData<String> confirm(@RequestParam("token") String token) {
        authService.confirmToken(token);
        return ResponseUtil.success(ResultCode.MSG_SUCCESSFUL_CONFIRMATION.message());
    }

    @Operation(summary = "Resend email verification")
    @ApiResponse(responseCode = "200",
            description = "Email verification resent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @GetMapping("/resend-verification-email")
    public ResponseData<String> resendVerificationEmail(@RequestParam("email") String email) {
        authService.resendVerificationEmail(email);
        return ResponseUtil.success(ResultCode.MSG_RESEND_SUCCESS.message());
    }

    @Operation(summary = "Request password reset link")
    @ApiResponse(responseCode = "200",
            description = "Password reset link sent successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/password-reset-request")
    public ResponseData<String> resetPasswordRequest(@RequestParam("email") String email) {
        var response = authService.resetPasswordLink(email);
        return ResponseUtil.success(response);
    }

    @Operation(summary = "Reset password")
    @ApiResponse(responseCode = "200",
            description = "Password reset successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reset-password")
    public ResponseData<String> resetPassword(@RequestParam("passwordResetToken") String passwordResetToken,
                                              @RequestBody PasswordResetRequest passwordResetRequest) {
        authService.resetPassword(passwordResetToken, passwordResetRequest);
        return ResponseUtil.success(ResultCode.MSG_RESET_PASSWORD_SUCCESS.message());
    }

    @Operation(summary = "Refresh access token")
    @ApiResponse(responseCode = "200",
            description = "Access token refreshed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/refresh-token")
    public ResponseData<TokenResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        var tokenResponse = authService.refreshToken(refreshToken);
        return ResponseUtil.success(tokenResponse);
    }

    @Operation(summary = "Logout")
    @ApiResponse(responseCode = "200",
            description = "Logout successful",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)))
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/logout")
    public ResponseData<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseUtil.success(ResultCode.MSG_LOGOUT_SUCCESS.message());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public AuthResponse getUserInfo(@RequestParam("accessToken") String accessToken) {
        return authService.getUserInfo(accessToken);
    }
}
