package org.crochet.service;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.crochet.exception.EmailVerificationException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.exception.TokenException;
import org.crochet.model.ConfirmationToken;
import org.crochet.model.PasswordResetToken;
import org.crochet.model.User;
import org.crochet.payload.request.LoginRequest;
import org.crochet.payload.request.PasswordResetRequest;
import org.crochet.payload.request.SignUpRequest;
import org.crochet.payload.response.AuthResponse;
import org.crochet.service.contact.AuthService;
import org.crochet.service.contact.ConfirmTokenService;
import org.crochet.service.contact.EmailSender;
import org.crochet.service.contact.PasswordResetTokenService;
import org.crochet.service.contact.TokenService;
import org.crochet.service.contact.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.crochet.constant.MessageConstant.ACTIVE_NOW;
import static org.crochet.constant.MessageConstant.CLICK_TO_ACTIVE_CONTENT;
import static org.crochet.constant.MessageConstant.CONFIRM_YOUR_EMAIL;
import static org.crochet.constant.MessageConstant.RESET_NOTIFICATION;
import static org.crochet.constant.MessageConstant.RESET_PASSWORD;
import static org.crochet.constant.MessageConstant.RESET_YOUR_PASSWORD_CONTENT;

/**
 * AuthServiceImpl class
 */
@Service
public class AuthServiceImpl implements AuthService {
  private final ConfirmTokenService confirmTokenService;
  private final PasswordResetTokenService passwordResetTokenService;
  private final UserService userService;
  private final TokenService tokenService;
  private final EmailSender emailSender;
  private final PasswordEncoder passwordEncoder;
  private final Gson gson;

  public AuthServiceImpl(ConfirmTokenService confirmTokenService,
                         PasswordResetTokenService passwordResetTokenService,
                         UserService userService,
                         TokenService tokenService,
                         EmailSender emailSender,
                         PasswordEncoder passwordEncoder,
                         Gson gson) {
    this.confirmTokenService = confirmTokenService;
    this.passwordResetTokenService = passwordResetTokenService;
    this.userService = userService;
    this.tokenService = tokenService;
    this.emailSender = emailSender;
    this.passwordEncoder = passwordEncoder;
    this.gson = gson;
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
    var user = userService.checkLogin(loginRequest.getEmail(), loginRequest.getPassword());
    var tokenResponse = tokenService.createToken(user);
    // Return the authentication token in an AuthResponse
    return AuthResponse.builder()
        .accessToken(tokenResponse.getJwtToken())
        .refreshToken(tokenResponse.getRefreshToken())
        .role(user.getRole().getValue())
        .email(user.getEmail())
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
    ConfirmationToken confirmationToken = confirmTokenService.createOrUpdateToken(user);

    // Build the base URI
    String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

    // Build the confirmation link
    String link = baseUri + "/auth/confirm?token=" + confirmationToken.getToken();

    // Send confirmation email
    emailSender.send(signUpRequest.getEmail(),
        CONFIRM_YOUR_EMAIL,
        buildEmailLink(signUpRequest.getEmail(), link, CONFIRM_YOUR_EMAIL, CLICK_TO_ACTIVE_CONTENT,
            ACTIVE_NOW));

    return "User registered successfully";
  }

  /**
   * Resend link to active
   *
   * @return A success API response.
   * @throws ResourceNotFoundException User not found
   */
  @Override
  public String resendVerificationEmail(String email) {
    // If user don't exist, ResourceNotFoundException will be thrown
    User user = userService.getByEmail(email);

    // Create or update confirmation token
    ConfirmationToken confirmationToken = confirmTokenService.createOrUpdateToken(user);

    // Build the base URI
    String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

    // Build the confirmation link
    String link = baseUri + "/auth/confirm?token=" + confirmationToken.getToken();

    // Send confirmation email
    emailSender.send(email, CONFIRM_YOUR_EMAIL,
        buildEmailLink(email, link, CONFIRM_YOUR_EMAIL, CLICK_TO_ACTIVE_CONTENT, ACTIVE_NOW));

    return "Resend successfully";
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
      throw new EmailVerificationException("Email already confirmed");
    }

    if (expiredAt.isBefore(now)) {
      throw new TokenException("Token expired");
    }

    // Update confirmedAt
    confirmTokenService.updateConfirmedAt(token, LocalDateTime.now());

    // Update emailVerified to true
    userService.verifyEmail(confirmationToken.getUser().getEmail());

    return "Successful confirmation";
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
        buildEmailLink(email, link, RESET_NOTIFICATION, RESET_YOUR_PASSWORD_CONTENT, RESET_PASSWORD);
    emailSender.send(email, RESET_NOTIFICATION, passwordResetLink);

    return "Send successfully with link reset password: " + link;
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
      throw new TokenException("Password reset token is expired");
    }

    // Get email by token
    String email = passwordResetTokenService.getEmailByToken(token);

    // Update new password for user
    var newPassword = passwordEncoder.encode(passwordResetRequest.getNewPassword());

    // Update user
    userService.updatePassword(newPassword, email);

    // Delete password reset token
    passwordResetTokenService.deletePasswordToken(passwordResetToken);

    return "Reset password successfully";
  }

  @Override
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    var tokenResponse = tokenService.refreshToken(request, response);
    var authResponse = AuthResponse.builder()
        .accessToken(tokenResponse.getJwtToken())
        .refreshToken(tokenResponse.getRefreshToken())
        .build();
    String jsonResponse = gson.toJson(authResponse);

    // Set Content-Type to application/json
    response.setContentType("application/json");

    // Write JSON to outputStream of HttpServletResponse
    response.getWriter().write(jsonResponse);
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    tokenService.logout(request, response);
  }
}
