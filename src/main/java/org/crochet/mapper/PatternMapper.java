package org.crochet.mapper;

import org.crochet.model.Pattern;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternResponse;
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
public interface PatternMapper extends PartialUpdate<Pattern, PatternRequest> {
    PatternMapper INSTANCE = Mappers.getMapper(PatternMapper.class);

    @Mapping(target = "isHome", source = "home")
    @Mapping(target = "currencyCode", source = "currencyCode")
    PatternResponse toResponse(Pattern pattern);

    @Override
    default Pattern partialUpdate(PatternRequest req, Pattern pattern) {
        if (pattern == null) {
            return null;
        }
        if (req.getName() != null) {
            pattern.setName(req.getName());
        }
        if (req.getDescription() != null) {
            pattern.setDescription(req.getDescription());
        }
        if (req.getPrice() != pattern.getPrice()) {
            pattern.setPrice(req.getPrice());
        }
        if (req.getCurrencyCode() != null) {
            pattern.setCurrencyCode(req.getCurrencyCode());
        }
        if (req.isHome() != pattern.isHome()) {
            pattern.setHome(req.isHome());
        }
        if (req.getLink() != null) {
            pattern.setLink(req.getLink());
        }
        if (req.getContent() != null) {
            pattern.setContent(req.getContent());
        }
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            var sortedImages = ImageUtils.sortFiles(req.getImages());
            pattern.setImages(FileMapper.INSTANCE.toEntities(sortedImages));
        }
        if (req.getFiles() != null && !req.getFiles().isEmpty()) {
            var sortedFiles = ImageUtils.sortFiles(req.getFiles());
            pattern.setFiles(FileMapper.INSTANCE.toSetEntities(sortedFiles));
        }
        return pattern;
    }

    List<PatternResponse> toResponses(Collection<Pattern> patterns);
}