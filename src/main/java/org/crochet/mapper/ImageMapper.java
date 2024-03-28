package org.crochet.mapper;

import org.crochet.model.File;
import org.crochet.model.Image;
import org.crochet.payload.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.Set;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {
    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    Image toEntity(FileResponse fileResponse);
    Set<Image> toEntities(Collection<FileResponse> fileResponses);

    FileResponse toResponse(Image image);
}