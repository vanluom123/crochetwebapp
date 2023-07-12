package org.crochet.mapper;

import org.crochet.mapper.abstraction.UserMapper;
import org.crochet.model.User;
import org.crochet.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
  @Override
  public UserResponse toUserResponse(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User is null");
    }

    return new UserResponse()
        .setId(user.getId())
        .setName(user.getName())
        .setEmail(user.getEmail())
        .setProviderId(user.getProviderId())
        .setImageUrl(user.getImageUrl())
        .setEmailVerified(user.getEmailVerified())
        .setVerificationCode(user.getVerificationCode());
  }

  @Override
  public User toUser(UserResponse response) {
    if (response == null) {
      throw new IllegalArgumentException("UserResponse is null");
    }

    return new User()
        .setId(response.getId())
        .setName(response.getName())
        .setEmail(response.getEmail())
        .setProviderId(response.getProviderId())
        .setImageUrl(response.getImageUrl())
        .setEmailVerified(response.getEmailVerified())
        .setVerificationCode(response.getVerificationCode());
  }
}
