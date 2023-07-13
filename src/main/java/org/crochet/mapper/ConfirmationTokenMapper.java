package org.crochet.mapper;

import org.crochet.model.ConfirmationToken;
import org.crochet.response.ConfirmationTokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConfirmationTokenMapper {
  ConfirmationTokenMapper INSTANCE = Mappers.getMapper(ConfirmationTokenMapper.class);

  @Mapping(target = "userResponse", source = "user")
  ConfirmationTokenResponse toConfirmationTokenResponse(ConfirmationToken confirmationToken);
}
