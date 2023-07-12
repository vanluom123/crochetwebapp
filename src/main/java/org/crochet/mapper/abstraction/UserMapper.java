package org.crochet.mapper.abstraction;

import org.crochet.model.User;
import org.crochet.response.UserResponse;

public interface UserMapper {
  UserResponse toUserResponse(User user);

  User toUser(UserResponse response);
}
