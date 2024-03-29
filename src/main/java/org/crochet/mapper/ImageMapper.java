package org.crochet.mapper;

import org.crochet.model.Image;
import org.crochet.payload.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {
    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    Image toEntity(FileResponse fileResponse);
    List<Image> toEntities(Collection<FileResponse> fileResponses);

    FileResponse toResponse(Image image);
}