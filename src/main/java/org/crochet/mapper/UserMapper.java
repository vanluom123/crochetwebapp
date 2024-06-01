package org.crochet.mapper;

import org.crochet.model.User;
import org.crochet.payload.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserResponse userResponse);

    UserResponse toResponse(User user);

    List<UserResponse> toResponses(List<User> users);
}