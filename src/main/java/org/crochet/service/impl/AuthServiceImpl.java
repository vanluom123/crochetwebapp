package org.crochet.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.crochet.exception.EmailVerificationException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.exception.TokenException;
import org.crochet.model.ConfirmationToken;
import org.crochet.model.PasswordResetToken;
import org.crochet.model.RefreshToken;
import org.crochet.model.User;
import org.crochet.payload.request.LoginRequest;
import org.crochet.payload.request.PasswordResetRequest;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.response.AuthResponse;
import org.crochet.payload.response.TokenResponse;
import org.crochet.service.AuthService;
import org.crochet.service.ConfirmTokenService;
import org.crochet.service.EmailSender;
import org.crochet.service.JwtTokenService;
import org.crochet.service.PasswordResetTokenService;
import org.crochet.service.RefreshTokenService;
import org.crochet.service.TokenBlacklistService;
import org.crochet.service.UserService;
import org.crochet.util.TokenUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

import java.time.LocalDateTime;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.ACTIVE_NOW;
import static org.crochet.constant.MessageConstant.CONFIRM_YOUR_EMAIL;
import static org.crochet.constant.MessageConstant.MSG_ACCOUNT_ACTIVATION_LINK;
import static org.crochet.constant.MessageConstant.MSG_EMAIL_ALREADY_CONFIRMED;
import static org.crochet.constant.MessageConstant.MSG_EMAIL_NOT_VERIFIED;
import static org.crochet.constant.MessageConstant.MSG_PASSWORD_RESET_TOKEN_EXPIRED;
import static org.crochet.constant.MessageConstant.MSG_RESEND_SUCCESS;
import static org.crochet.constant.MessageConstant.MSG_RESET_PASSWORD_LINK;
import static org.crochet.constant.MessageConstant.MSG_RESET_PASSWORD_SUCCESS;
import static org.crochet.constant.MessageConstant.MSG_SUCCESSFUL_CONFIRMATION;
import static org.crochet.constant.MessageConstant.MSG_TOKEN_EXPIRED;
import static org.crochet.constant.MessageConstant.MSG_USER_REGISTER_SUCCESS;
import static org.crochet.constant.MessageConstant.REFRESH_TOKEN_NOT_IN_DB;
import static org.crochet.constant.MessageConstant.RESET_NOTIFICATION;
import static org.crochet.constant.MessageConstant.RESET_PASSWORD;
import static org.crochet.constant.MessageConstant.RESET_PASSWORD_LINK;
import static org.springframework.util.StringUtils.hasText;

/**
 * AuthServiceImpl class
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final ConfirmTokenService confirmTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserService userService;
    private final EmailSender emailSender;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenService jwtTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Constructor
     *
     * @param confirmTokenService       ConfirmTokenService
     * @param passwordResetTokenService PasswordResetTokenService
     * @param userService               UserService
     * @param emailSender               EmailSender
     * @param refreshTokenService       RefreshTokenService
     * @param jwtTokenService           JwtTokenService
     * @param tokenBlacklistService     TokenBlacklistService
     */
    public AuthServiceImpl(ConfirmTokenService confirmTokenService,
                           PasswordResetTokenService passwordResetTokenService,
                           UserService userService,
                           EmailSender emailSender,
                           RefreshTokenService refreshTokenService,
                           JwtTokenService jwtTokenService,
                           TokenBlacklistService tokenBlacklistService) {
        this.confirmTokenService = confirmTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.userService = userService;
        this.emailSender = emailSender;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenService = jwtTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /**
     * Authenticates a user based on the provided login credentials.
     *
     * @param loginRequest The login request containing the user's email and password.
     * @return An AuthResponse containing the authentication token.
     */
    @Override
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        // Check email and password
        var user = userService.validateUserCredentials(loginRequest.getEmail(), loginRequest.getPassword());
        // Check email verified
        if (!user.isEmailVerified()) {
            throw new EmailVerificationException(MSG_EMAIL_NOT_VERIFIED, MAP_CODE.get(MSG_EMAIL_NOT_VERIFIED));
        }
        // Create refresh token
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        // Get access token
        var accessToken = jwtTokenService.generateToken(user.getId());
        // Return the authentication token in an AuthResponse
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .role(user.getRole().getValue())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .userId(user.getId())
                .build();
    }

    /**
     * Registers a new user based on the provided sign-up request.
     *
     * @param signUpRequest The sign-up request containing user information.
     * @return A success API response.
     */
    @Override
    @Transactional
    public String registerUser(SignUpRequest signUpRequest) {
        // Create or update user
        User user = userService.createUser(signUpRequest);

        // Create or update confirmation token
        ConfirmationToken confirmationToken = confirmTokenService.createOrUpdate(user);

        // Build the base URI
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

        // Build the confirmation link
        String link = baseUri + "/auth/confirm?token=" + confirmationToken.getToken();

        // Send confirmation email
        emailSender.send(signUpRequest.getEmail(),
                CONFIRM_YOUR_EMAIL,
                buildEmailLink(signUpRequest.getEmail(), link, CONFIRM_YOUR_EMAIL, MSG_ACCOUNT_ACTIVATION_LINK,
                        ACTIVE_NOW));

        return MSG_USER_REGISTER_SUCCESS;
    }

    /**
     * Resend link to active
     *
     * @return A success API response.
     * @throws ResourceNotFoundException User not found
     */
    @Override
    @RateLimiter(name = "resendEmail", fallbackMethod = "resendVerificationEmailFallback")
    @Retry(name = "resendEmail")
    public String resendVerificationEmail(String email) {
        // If user don't exist, ResourceNotFoundException will be thrown
        User user = userService.getByEmail(email);

        // Create or update confirmation token
        ConfirmationToken confirmationToken = confirmTokenService.createOrUpdate(user);

        // Build the base URI
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

        // Build the confirmation link
        String link = baseUri + "/auth/confirm?token=" + confirmationToken.getToken();

        // Send confirmation email
        emailSender.send(email, CONFIRM_YOUR_EMAIL,
                buildEmailLink(email, link, CONFIRM_YOUR_EMAIL, MSG_ACCOUNT_ACTIVATION_LINK, ACTIVE_NOW));

        return MSG_RESEND_SUCCESS;
    }

    /**
     * Confirmation token
     *
     * @param token Token
     * @return ApiResponse
     * @throws EmailVerificationException Email already confirmed
     * @throws TokenException             Token expired
     */
    @Transactional
    @Override
    public String confirmToken(String token) {
        var confirmationToken = confirmTokenService.getToken(token);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        LocalDateTime confirmedAt = confirmationToken.getConfirmedAt();

        if (confirmedAt != null) {
            throw new EmailVerificationException(MSG_EMAIL_ALREADY_CONFIRMED, MAP_CODE.get(MSG_EMAIL_ALREADY_CONFIRMED));
        }

        if (expiredAt.isBefore(now)) {
            throw new TokenException(MSG_TOKEN_EXPIRED, MAP_CODE.get(MSG_TOKEN_EXPIRED));
        }

        // Update confirmedAt
        confirmTokenService.updateConfirmedAt(token, LocalDateTime.now());

        // Update emailVerified to true
        userService.verifyEmail(confirmationToken.getUser().getEmail());

        return MSG_SUCCESSFUL_CONFIRMATION;
    }

    /**
     * Build email link
     *
     * @param name         Name of user or email
     * @param link         Link
     * @param subjectEmail Subject email
     * @param contentEmail Content email
     * @param contentLink  Content link
     * @return Email link
     */
    private String buildEmailLink(String name, String link, String subjectEmail, String contentEmail,
                                  String contentLink) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">" +
                subjectEmail + "</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name +
                ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> " + contentEmail +
                " </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" +
                link + "\">" + contentLink +
                "</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    /**
     * Reset password link
     *
     * @param email Email
     * @return ApiResponse
     * @throws ResourceNotFoundException User not found
     */
    @Transactional
    @Override
    @RateLimiter(name = "passwordResetRateLimiter", fallbackMethod = "resetPasswordLinkFallback")
    public String resetPasswordLink(String email) {
        // Check user
        User user = userService.getByEmail(email);

        // Build password reset token
        var passwordResetToken = passwordResetTokenService.createOrUpdatePasswordResetToken(user);

        // Build the base URI
        String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        String link = baseUri + "/auth/reset-password?passwordResetToken=" + passwordResetToken.getToken();

        // Send password reset link to email
        var passwordResetLink =
                buildEmailLink(email, link, RESET_NOTIFICATION, MSG_RESET_PASSWORD_LINK, RESET_PASSWORD);
        emailSender.send(email, RESET_NOTIFICATION, passwordResetLink);

        return RESET_PASSWORD_LINK + link;
    }

    /**
     * Reset password
     *
     * @param token                Token
     * @param passwordResetRequest PasswordResetRequest
     * @return ApiResponse
     * @throws TokenException            Password reset token is expired
     * @throws ResourceNotFoundException User not found with current token
     */
    @Transactional
    @Override
    public String resetPassword(String token, PasswordResetRequest passwordResetRequest) {
        // Get PasswordResetToken
        PasswordResetToken passwordResetToken = passwordResetTokenService.getPasswordResetToken(token);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = passwordResetToken.getExpiresAt();

        if (expiredAt.isBefore(now)) {
            throw new TokenException(MSG_PASSWORD_RESET_TOKEN_EXPIRED, MAP_CODE.get(MSG_PASSWORD_RESET_TOKEN_EXPIRED));
        }

        // Get email by token
        String email = passwordResetTokenService.getEmailByToken(token);

        // Update user
        userService.updatePassword(passwordResetRequest.getNewPassword(), email);

        // Delete password reset token
        passwordResetTokenService.deletePasswordToken(passwordResetToken);

        return MSG_RESET_PASSWORD_SUCCESS;
    }

    /**
     * Refresh token
     *
     * @param refreshToken Refresh token
     * @return TokenResponse
     * @throws ResourceNotFoundException Refresh token not in database
     */
    @Override
    public TokenResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtTokenService.generateToken(user.getId());
                    return TokenResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                }).orElseThrow(() ->
                        new ResourceNotFoundException(REFRESH_TOKEN_NOT_IN_DB,
                                MAP_CODE.get(REFRESH_TOKEN_NOT_IN_DB))
                );
    }

    /**
     * Logout
     *
     * @param request HttpServletRequest
     */
    @Override
    public void logout(HttpServletRequest request) {
        var token = TokenUtils.getJwtFromAuthorizationHeader(request);
        if (hasText(token)) {
            tokenBlacklistService.addTokenToBlacklist(token);
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Fallback method for resendVerificationEmail
     *
     * @param email Email
     * @param t     Throwable
     * @return Fallback message
     */
    @SuppressWarnings("unused")
    private String resetPasswordLinkFallback(String email, Throwable t) {
        return "Request limit exceeded. Please wait for a while before retrying.";
    }

    /**
     * Fallback method for resendVerificationEmail
     *
     * @param email Email
     * @param t     Throwable
     * @return Fallback message
     */
    @SuppressWarnings("unused")
    private String resendVerificationEmailFallback(String email, Throwable t) {
        return "Request limit exceeded. Please wait for a while before retrying.";
    }

    /**
     * Get refresh token expires at
     *
     * @param refreshToken Refresh token
     * @return LocalDateTime
     * @throws ResourceNotFoundException Refresh token not in database
     */
    @Override
    public LocalDateTime getRefreshTokenExpiresAt(String refreshToken) {
        var refresh = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException(REFRESH_TOKEN_NOT_IN_DB,
                        MAP_CODE.get(REFRESH_TOKEN_NOT_IN_DB)));
        return refresh.getExpiresAt();
    }
}
