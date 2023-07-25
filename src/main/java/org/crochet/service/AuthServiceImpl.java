package org.crochet.service;

import org.crochet.exception.BadRequestException;
import org.crochet.exception.EmailVerificationException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.exception.TokenException;
import org.crochet.model.AuthProvider;
import org.crochet.model.ConfirmationToken;
import org.crochet.model.User;
import org.crochet.repository.ConfirmationTokenRepository;
import org.crochet.repository.UserRepository;
import org.crochet.request.LoginRequest;
import org.crochet.request.SignUpRequest;
import org.crochet.response.ApiResponse;
import org.crochet.response.AuthResponse;
import org.crochet.security.TokenProvider;
import org.crochet.service.abstraction.AuthService;
import org.crochet.service.abstraction.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class AuthServiceImpl implements AuthService {
  private final AuthenticationManager authenticationManager;
  private final TokenProvider tokenProvider;
  private final EmailSender emailSender;
  private final ConfirmationTokenRepository confirmationTokenRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public AuthServiceImpl(AuthenticationManager authenticationManager,
                         TokenProvider tokenProvider,
                         EmailSender emailSender,
                         ConfirmationTokenRepository confirmationTokenRepository,
                         UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    this.emailSender = emailSender;
    this.confirmationTokenRepository = confirmationTokenRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
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
    String token = tokenProvider.createToken(authentication);

    // Return the authentication token in an AuthResponse
    return new AuthResponse(token);
  }

  /**
   * Registers a new user based on the provided sign-up request.
   *
   * @param signUpRequest The sign-up request containing user information.
   * @return A success API response.
   * @throws IllegalStateException If the email is not valid.
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
    emailSender.send(signUpRequest.getEmail(), buildEmail(signUpRequest.getName(), link));

    return new ApiResponse(true, "User registered successfully");
  }

  /**
   * Resend link to active
   *
   * @return A success API response.
   */
  @Override
  public ApiResponse resendVerificationEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Create or update confirmation token
    ConfirmationToken confirmationToken = createOrUpdateToken(user);

    // Build the base URI
    String baseUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

    // Build the confirmation link
    String link = baseUri + "/auth/confirm?token=" + confirmationToken.getToken();

    // Send confirmation email
    emailSender.send(email, buildEmail(email, link));

    return new ApiResponse(true, "Resend successfully");
  }

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

  private String buildEmail(String name, String link) {
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
        "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
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
        "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
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

  private ConfirmationToken getToken(String token) {
    return confirmationTokenRepository.findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
  }

  private boolean isEmailPresent(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  private ConfirmationToken createOrUpdateToken(User user) {
    ConfirmationToken confirmationToken = confirmationTokenRepository
        .findByUser(user)
        .orElse(null);

    if (confirmationToken == null) {
      // Create a new token
      String token = UUID.randomUUID().toString();
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime expirationTime = now.plusMinutes(15);

      confirmationToken = new ConfirmationToken()
          .setToken(token)
          .setCreatedAt(now)
          .setExpiresAt(expirationTime)
          .setUser(user);
    } else {
      // Update the existing token
      confirmationToken.setToken(UUID.randomUUID().toString())
          .setCreatedAt(LocalDateTime.now())
          .setExpiresAt(LocalDateTime.now().plusMinutes(15));
    }

    return confirmationTokenRepository.save(confirmationToken);
  }

  private User createUser(SignUpRequest signUpRequest) {
    // Check if the email address is already in use
    if (isEmailPresent(signUpRequest.getEmail())) {
      throw new BadRequestException("Email address already in use");
    }

    // Creating user's account
    User user = new User()
        .setName(signUpRequest.getName())
        .setEmail(signUpRequest.getEmail())
        .setPassword(passwordEncoder.encode(signUpRequest.getPassword()))
        .setProvider(AuthProvider.local);

    // Save the user to the repository
    return userRepository.save(user);
  }
}
