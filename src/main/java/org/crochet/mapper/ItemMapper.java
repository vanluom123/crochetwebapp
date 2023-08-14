package org.crochet.mapper;

import org.crochet.model.Item;
import org.crochet.response.ItemResponse;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Base64;
import java.util.List;

@Mapper
public interface ItemMapper {
  ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

  @InheritInverseConfiguration
  @Mapping(target = "image", source = "image", qualifiedByName = "decoding")
  ItemResponse toResponse(Item item);

  @Mapping(target = "image", source = "image", qualifiedByName = "encoding")
  Item toItem(ItemResponse item);

  List<ItemResponse> toResponses(List<Item> items);

  @Named("encoding")
  default String encoding(byte[] data) {
    return Base64.getEncoder().encodeToString(data);
  }

  @Named("decoding")
  default byte[] decoding(String data) {
    return Base64.getDecoder().decode(data);
  }
}
