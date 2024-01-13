package org.crochet.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.crochet.enumerator.TokenType;
import org.crochet.model.Token;
import org.crochet.model.User;
import org.crochet.payload.response.TokenResponse;
import org.crochet.repository.TokenRepo;
import org.crochet.security.CustomUserDetailsService;
import org.crochet.service.contact.JwtTokenService;
import org.crochet.service.contact.TokenService;
import org.crochet.service.contact.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
  private final TokenRepo tokenRepo;
  private final UserService userService;
  private final JwtTokenService jwtTokenService;
  private final CustomUserDetailsService customUserDetailsService;

  @Override
  public Optional<Token> getByToken(String token) {
    return tokenRepo.findByToken(token);
  }

  @Override
  public TokenResponse createToken(User user) {
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
    var jwtToken = jwtTokenService.generateToken(userDetails);
    var refreshToken = jwtTokenService.generateRefreshToken(userDetails);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return TokenResponse.builder()
        .jwtToken(jwtToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public TokenResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      final String refreshToken = authHeader.substring(7);
      final String userEmail = jwtTokenService.extractUsername(refreshToken);

      if (userEmail != null) {
        var user = userService.getByEmail(userEmail);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        if (jwtTokenService.isTokenValid(refreshToken, userDetails)) {
          var accessToken = jwtTokenService.generateToken(userDetails);
          revokeAllUserTokens(user);
          saveUserToken(user, accessToken);
          return TokenResponse.builder()
              .jwtToken(accessToken)
              .refreshToken(refreshToken)
              .build();
        }
      }
    }

    return null;
  }

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String jwt = authHeader.substring(7);

      Optional<Token> storedToken = tokenRepo.findByToken(jwt);

      storedToken.ifPresent(token -> {
        token.setExpired(true);
        token.setRevoked(true);
        tokenRepo.save(token);
        SecurityContextHolder.clearContext();
      });
    }
  }

  private void saveUserToken(User user, String jwtToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepo.save(token);
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepo.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepo.saveAll(validUserTokens);
  }
}
