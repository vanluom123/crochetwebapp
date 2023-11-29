package org.crochet.mapper;

import org.crochet.model.Pattern;
import org.crochet.model.PatternImage;
import org.crochet.request.PatternRequest;
import org.crochet.response.PatternResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class PatternMapperImpl implements PatternMapper {
    @Override
    public PatternResponse toResponse(Pattern pattern) {
        if (pattern == null) {
            return null;
        }

        var images = pattern.getPatternImages()
                .stream()
                .map(PatternImage::getImageUrl)
                .toList();

        return PatternResponse.builder()
                .id(pattern.getId())
                .name(pattern.getName())
                .description(pattern.getDescription())
                .price(pattern.getPrice())
                .images(images)
                .build();
    }

    @Override
    public Pattern toPattern(PatternRequest request) {
        if (request == null) {
            return null;
        }

        return Pattern.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();
    }

    @Override
    public List<PatternResponse> toResponses(List<Pattern> patterns) {
        if (ObjectUtils.isEmpty(patterns)) {
            return null;
        }

        return patterns.stream()
                .map(this::toResponse)
                .toList();
    }
}
