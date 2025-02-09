package org.crochet.mapper;

import org.crochet.model.FreePattern;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.util.ImageUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FileMapper.class, CategoryMapper.class}
)
public interface FreePatternMapper {
    FreePatternMapper INSTANCE = Mappers.getMapper(FreePatternMapper.class);

    @Mapping(target = "isHome", source = "home")
    FreePatternResponse toResponse(FreePattern pattern);

    List<FreePatternResponse> toResponses(Collection<FreePattern> freePatterns);

    default FreePattern update(FreePatternRequest req, FreePattern freePattern) {
        if (freePattern == null) {
            return null;
        }
        if (req.getName() != null) {
            freePattern.setName(req.getName());
        }
        if (req.getDescription() != null) {
            freePattern.setDescription(req.getDescription());
        }
        if (req.getAuthor() != null) {
            freePattern.setAuthor(req.getAuthor());
        }
        if (req.isHome() != freePattern.isHome()) {
            freePattern.setHome(req.isHome());
        }
        if (req.getLink() != null) {
            freePattern.setLink(req.getLink());
        }
        if (req.getContent() != null) {
            freePattern.setContent(req.getContent());
        }
        if (req.getStatus() != null) {
            freePattern.setStatus(req.getStatus());
        }
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            var sortedImages = ImageUtils.sortFiles(req.getImages());
            freePattern.setImages(FileMapper.INSTANCE.toEntities(sortedImages));
        }
        if (req.getFiles() != null && !req.getFiles().isEmpty()) {
            var sortedFiles = ImageUtils.sortFiles(req.getFiles());
            freePattern.setFiles(FileMapper.INSTANCE.toSetEntities(sortedFiles));
        }
        return freePattern;
    }
}