package org.crochet.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.crochet.exception.BadRequestException;
import org.crochet.exception.EmailVerificationException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.exception.TokenException;
import org.crochet.model.AuthProvider;
import org.crochet.model.ConfirmationToken;
import org.crochet.model.PasswordResetToken;
import org.crochet.model.User;
import org.crochet.repository.ConfirmationTokenRepository;
import org.crochet.repository.PasswordResetTokenRepository;
import org.crochet.repository.UserRepository;
import org.crochet.request.LoginRequest;
import org.crochet.request.PasswordResetRequest;
import org.crochet.request.SignUpRequest;
import org.crochet.response.ApiResponse;
import org.crochet.response.AuthResponse;
import org.crochet.service.abstraction.AuthService;
import org.crochet.service.abstraction.EmailSender;
import org.crochet.service.abstraction.TokenService;
import org.crochet.util.CookieUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AuthServiceImpl class
 */
@Service
public class AuthServiceImpl implements AuthService {
  private static final String ACTIVE_NOW = "Active now";
  private static final String CLICK_TO_ACTIVE_CONTENT = "Thank you for registering. Please click on the below link to activate your account:";
  private static final String CONFIRM_YOUR_EMAIL = "Confirm your email";
  public static final String RESET_YOUR_PASSWORD_CONTENT = "Please click the below link to reset your password";
  public static final String RESET_PASSWORD = "Reset password";
  public static final String RESET_NOTIFICATION = "Password Reset Notification";
  public static final int JWT_TOKEN_MAX_AGE = 900;
  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;
  private final EmailSender emailSender;
  private final ConfirmationTokenRepository confirmationTokenRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final HttpServletResponse servletResponse;
  private final HttpServletRequest servletRequest;

  /**
   * Constructor
   *
   * @param authenticationManager        AuthenticationManager
   * @param tokenService                TokenProvider
   * @param emailSender                  EmailSender
   * @param confirmationTokenRepository  ConfirmationTokenRepository
   * @param userRepository               UserRepository
   * @param passwordEncoder              PasswordEncoder
   * @param passwordResetTokenRepository PasswordResetTokenRepository
   * @param servletResponse              HttpServletResponse
   * @param servletRequest               HttpServletRequest
   */
  public AuthServiceImpl(AuthenticationManager authenticationManager,
                         TokenService tokenService,
                         EmailSender emailSender,
                         ConfirmationTokenRepository confirmationTokenRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         PasswordResetTokenRepository passwordResetTokenRepository,
                         HttpServletResponse servletResponse,
                         HttpServletRequest servletRequest) {
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
    this.emailSender = emailSender;
    this.confirmationTokenRepository = confirmationTokenRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.passwordResetTokenRepository = passwordResetTokenRepository;
    this.servletResponse = servletResponse;
    this.servletRequest = servletRequest;
  }

  /**
   * Authenticates a user based on the provided login credentials.
   *
   * @param loginRequest The login request containing the user's email and password.
   * @return An AuthResponse containing the authentication token.
   */
  @Override
  public AuthResponse authenticateUser(LoginRequest loginRequest) {
    // Create an authentication token with the provided email and password
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
    // Authenticate the token using the authentication manager
    Authentication authentication = authenticationManager.authenticate(authToken);

    // Set the authenticated authentication object in the security context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Create a token for the authenticated user
    String token = tokenService.createToken(authentication);

    // Add cookie for jwtToken
    CookieUtils.addCookie(servletResponse, "jwtToken", token, JWT_TOKEN_MAX_AGE);

    // Return the authentication token in an AuthResponse
    return new AuthResponse(token);
  }

  /**
   * Registers a new user based on the provided sign-up request.
   *
   * @param signUpRequest The sign-up request containing user information.
   * @return A success API response.
   */
  @Override
  @Transactional
  public ApiResponse registerUser(SignUpRequest signUpRequest) {
    // Create or update user
    User user = createUser(signUpRequest);

    // Create or update confirmation token
    ConfirmationToken confirmationToken = createOrUpdateToken(user);

    // Build the base URI
    String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

    // Build the confirmation link
    String link = baseUri + "/auth/confirm?token=" + confirmationToken.getToken();

    // Send confirmation email
    emailSender.send(signUpRequest.getEmail(),
        CONFIRM_YOUR_EMAIL,
        buildEmailLink(signUpRequest.getEmail(), link, CONFIRM_YOUR_EMAIL, CLICK_TO_ACTIVE_CONTENT, ACTIVE_NOW));

    return new ApiResponse(true, "User registered successfully");
  }

  /**
   * Resend link to active
   *
   * @return A success API response.
   * @throws ResourceNotFoundException User not found
   */
  @Override
  public ApiResponse resendVerificationEmail(String email) {
    // If user don't exist, ResourceNotFoundException will be thrown
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Create or update confirmation token
    ConfirmationToken confirmationToken = createOrUpdateToken(user);

    // Build the base URI
    String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

    // Build the confirmation link
    String link = baseUri + "/auth/confirm?token=" + confirmationToken.getToken();

    // Send confirmation email
    emailSender.send(email, CONFIRM_YOUR_EMAIL, buildEmailLink(email, link, CONFIRM_YOUR_EMAIL, CLICK_TO_ACTIVE_CONTENT, ACTIVE_NOW));

    return new ApiResponse(true, "Resend successfully");
  }

  /**
   * Confirmation token
   *
   * @param token Token
   * @return ApiResponse
   * @throws EmailVerificationException Email already confirmed
   * @throws TokenException Token expired
   */
  @Transactional
  @Override
  public ApiResponse confirmToken(String token) {
    var confirmationToken = getToken(token);

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
    confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());

    // Update emailVerified to true
    userRepository.verifyEmail(confirmationToken.getUser().getEmail());

    return new ApiResponse(true, "Successfully confirmation");
  }

  /**
   * Build email link
   *
   * @param name Name of user or email
   * @param link Link
   * @param subjectEmail Subject email
   * @param contentEmail Content email
   * @param contentLink Content link
   * @return Email link
   */
  private String buildEmailLink(String name, String link, String subjectEmail, String contentEmail, String contentLink) {
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
        "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">" + subjectEmail + "</span>\n" +
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
        "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> " + contentEmail + " </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">" + contentLink + "</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
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
   * Get confirmation token
   *
   * @param token token
   * @return ConfirmationToken
   * @throws ResourceNotFoundException Token not found
   */
  private ConfirmationToken getToken(String token) {
    return confirmationTokenRepository.findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
  }

  /**
   * Check email is valid
   *
   * @param email Email
   * @return true if email exist
   */
  private boolean isValidEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  /**
   * Create or update confirmation token
   *
   * @param user User
   * @return ConfirmationToken
   */
  private ConfirmationToken createOrUpdateToken(User user) {
    ConfirmationToken confirmationToken = confirmationTokenRepository
        .findByUser(user)
        .orElse(null);

    String token = UUID.randomUUID().toString();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expirationTime = now.plusMinutes(15);

    if (confirmationToken == null) {
      // Create a new token
      confirmationToken = ConfirmationToken.builder()
          .token(token)
          .createdAt(now)
          .expiresAt(expirationTime)
          .user(user)
          .build();
    } else {
      // Update the existing token
      confirmationToken.setToken(token);
      confirmationToken.setCreatedAt(now);
      confirmationToken.setExpiresAt(expirationTime);
    }

    return confirmationTokenRepository.save(confirmationToken);
  }

  /**
   * Create user
   *
   * @param signUpRequest SignUpRequest
   * @return User
   * @throws BadRequestException Email address already in use
   */
  private User createUser(SignUpRequest signUpRequest) {
    // Check if the email address is already in use
    if (isValidEmail(signUpRequest.getEmail())) {
      throw new BadRequestException("Email address already in use");
    }

    // Creating user's account
    User user = User.builder()
        .name(signUpRequest.getName())
        .email(signUpRequest.getEmail())
        .emailVerified(false)
        .password(passwordEncoder.encode(signUpRequest.getPassword()))
        .provider(AuthProvider.local)
        .role(signUpRequest.getRole())
        .build();
    // Save the user to the repository
    return userRepository.save(user);
  }

  /**
   * Create or update password reset token
   *
   * @param user User
   * @return PasswordResetToken
   */
  private PasswordResetToken createOrUpdatePasswordResetToken(User user) {
    var passwordResetToken = passwordResetTokenRepository.findByUser(user)
        .orElse(null);

    String token = UUID.randomUUID().toString();
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expirationTime = now.plusMinutes(15);

    if (passwordResetToken == null) {
      // Create a new token
      passwordResetToken = PasswordResetToken.builder()
          .token(token)
          .createdAt(now)
          .expiresAt(expirationTime)
          .user(user)
          .build();
    } else {
      passwordResetToken.setToken(token);
      passwordResetToken.setCreatedAt(now);
      passwordResetToken.setExpiresAt(expirationTime);
    }

    return passwordResetTokenRepository.save(passwordResetToken);
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
  public ApiResponse resetPasswordLink(String email) {
    // Check user
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    // Build password reset token
    var passwordResetToken = createOrUpdatePasswordResetToken(user);

    // Build the base URI
    String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
    String link = baseUri + "/auth/reset-password?passwordResetToken=" + passwordResetToken.getToken();

    // Send password reset link to email
    var passwordResetLink = buildEmailLink(email, link, RESET_NOTIFICATION, RESET_YOUR_PASSWORD_CONTENT, RESET_PASSWORD);
    emailSender.send(email, RESET_NOTIFICATION, passwordResetLink);

    return new ApiResponse(true, "Send successfully with link reset password: " + link);
  }

  /**
   * Reset password
   *
   * @param token Token
   * @param passwordResetRequest PasswordResetRequest
   * @return ApiResponse
   * @throws TokenException Password reset token is expired
   * @throws ResourceNotFoundException User not found with current token
   */
  @Transactional
  @Override
  public ApiResponse resetPassword(String token, PasswordResetRequest passwordResetRequest) {
    // Get PasswordResetToken
    PasswordResetToken passwordResetToken = getPasswordResetToken(token);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiredAt = passwordResetToken.getExpiresAt();

    if (expiredAt.isBefore(now)) {
      throw new TokenException("Password reset token is expired");
    }

    // Get user by token
    User user = passwordResetTokenRepository.getUserByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with token: " + token));

    // Update new password for user
    user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));

    // Update user
    userRepository.save(user);

    // Delete password reset token
    passwordResetTokenRepository.delete(passwordResetToken);

    // Delete token from cookie
    CookieUtils.deleteCookie(servletRequest, servletResponse, "jwtToken");

    return new ApiResponse(true, "Reset password successfully");
  }

  /**
   * Get password reset token
   *
   * @param token token
   * @return PasswordResetToken
   * @throws ResourceNotFoundException Password reset token not found
   */
  private PasswordResetToken getPasswordResetToken(String token) {
    return passwordResetTokenRepository.findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Password reset token not found"));
  }
}
