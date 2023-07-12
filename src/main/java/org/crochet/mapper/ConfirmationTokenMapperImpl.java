package org.crochet.mapper;

import org.crochet.mapper.abstraction.ConfirmationTokenMapper;
import org.crochet.mapper.abstraction.UserMapper;
import org.crochet.model.ConfirmationToken;
import org.crochet.response.ConfirmationTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmationTokenMapperImpl implements ConfirmationTokenMapper {
  private final UserMapper userMapper;

  @Autowired
  public ConfirmationTokenMapperImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public ConfirmationTokenResponse toConfirmationTokenResponse(ConfirmationToken confirmationToken) {
    if (confirmationToken == null) {
      throw new IllegalArgumentException("Confirmation token is null");
    }

    return new ConfirmationTokenResponse()
        .setToken(confirmationToken.getToken())
        .setConfirmedAt(confirmationToken.getConfirmedAt())
        .setExpiresAt(confirmationToken.getExpiresAt())
        .setCreatedAt(confirmationToken.getCreatedAt())
        .setUserResponse(userMapper.toUserResponse(confirmationToken.getUser()));
  }
}
