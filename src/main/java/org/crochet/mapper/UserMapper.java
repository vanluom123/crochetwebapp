package org.crochet.mapper;

import org.crochet.model.User;
import org.crochet.request.UserRequest;
import org.crochet.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  UserResponse toUserResponse(User user);

  @Mapping(target = "confirmationTokens", ignore = true)
  User toUser(UserResponse response);

  @Mapping(target = "confirmationTokens", ignore = true)
  User toUser(UserRequest userRequest);

  UserRequest toUserRequest(User user);

  UserRequest toUserRequest(UserResponse userResponse);
}
