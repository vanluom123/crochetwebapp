package org.crochet.mapper;

import org.crochet.model.File;
import org.crochet.payload.response.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMapper {
    FileMapper INSTANCE = Mappers.getMapper(FileMapper.class);

    File toEntity(FileResponse fileResponse);
    List<File> toEntities(Collection<FileResponse> fileResponses);

    FileResponse toResponse(File file);
    List<FileResponse> toResponses(Collection<File> files);
}