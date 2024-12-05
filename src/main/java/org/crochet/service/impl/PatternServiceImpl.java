package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Pattern;
import org.crochet.model.Settings;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternOnHome;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.GenericFilter;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.SettingsRepo;
import org.crochet.service.PatternService;
import org.crochet.util.ImageUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

/**
 * PatternServiceImpl class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatternServiceImpl implements PatternService {
    private final PatternRepository patternRepo;
    private final CategoryRepo categoryRepo;
    private final SettingsRepo settingsRepo;

    /**
     * Create or update pattern
     *
     * @param request PatternRequest
     */
    @Transactional
    @Override
    public PatternResponse createOrUpdate(PatternRequest request) {
        Pattern pattern;
        var images = ImageUtils.sortFiles(request.getImages());
        var files = ImageUtils.sortFiles(request.getFiles());

        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_CATEGORY_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_CATEGORY_NOT_FOUND)));
            pattern = Pattern.builder()
                    .category(category)
                    .name(request.getName())
                    .price(request.getPrice())
                    .description(request.getDescription())
                    .currencyCode(request.getCurrencyCode())
                    .isHome(request.isHome())
                    .link(request.getLink())
                    .content(request.getContent())
                    .files(FileMapper.INSTANCE.toSetEntities(files))
                    .images(FileMapper.INSTANCE.toEntities(images))
                    .build();
        } else {
            pattern = patternRepo.findById(request.getId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_PATTERN_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_PATTERN_NOT_FOUND)));
            pattern.setName(request.getName());
            pattern.setDescription(request.getDescription());
            pattern.setPrice(request.getPrice());
            pattern.setCurrencyCode(request.getCurrencyCode());
            pattern.setLink(request.getLink());
            pattern.setHome(request.isHome());
            pattern.setContent(request.getContent());
            pattern.setFiles(FileMapper.INSTANCE.toSetEntities(files));
            pattern.setImages(FileMapper.INSTANCE.toEntities(images));
        }

        pattern = patternRepo.save(pattern);

        return PatternResponse.builder()
                .id(pattern.getId())
                .name(pattern.getName())
                .price(pattern.getPrice())
                .description(pattern.getDescription())
                .currencyCode(pattern.getCurrencyCode())
                .link(pattern.getLink())
                .content(pattern.getContent())
                .build();
    }

    /**
     * Get patterns
     *
     * @param pageNo   Page number
     * @param pageSize The size of page
     * @param sortBy   Sort by
     * @param sortDir  Sort directory
     * @param filters  The list of filters
     * @return Pattern is paginated
     */
    @Override
    public PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 Filter[] filters) {
        Specification<Pattern> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<Pattern> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        var patternIds = patternRepo.findAll(spec)
                .stream()
                .map(Pattern::getId)
                .toList();

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        var page = patternRepo.findPatternOnHomeWithIds(patternIds, pageable);

        return PatternPaginationResponse.builder()
                .contents(page.getContent())
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    /**
     * Get limited patterns
     *
     * @return List of PatternResponse
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<PatternOnHome> getLimitedPatterns() {
        List<Settings> settings = settingsRepo.findSettings();
        if (settings == null || settings.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Settings> settingsMap = settings.stream()
                .collect(Collectors.toMap(Settings::getKey, Function.identity()));

        var direction = settingsMap.get("homepage.pattern.direction").getValue();

        var orderBy = settingsMap.get("homepage.pattern.orderBy").getValue();

        var limit = settingsMap.get("homepage.pattern.limit").getValue();

        Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable pageable = PageRequest.of(0, Integer.parseInt(limit), sort);
        return patternRepo.findLimitedNumPattern(pageable);
    }

    /**
     * Get pattern ids
     *
     * @param pageNo Page number
     * @param limit  Limit
     * @return List of pattern ids
     */
    @Override
    public List<String> getPatternIds(int pageNo, int limit) {
        Pageable pageable = PageRequest.of(pageNo, limit);
        return patternRepo.getPatternIds(pageable);
    }

    /**
     * Get pattern detail
     *
     * @param id Pattern id
     * @return PatternResponse
     */
    @Override
    public PatternResponse getDetail(String id) {
        var pattern = patternRepo.getDetail(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_PATTERN_NOT_FOUND)));
        return PatternMapper.INSTANCE.toResponse(pattern);
    }

    /**
     * Delete pattern
     *
     * @param id Pattern id
     */
    @Transactional
    @Override
    public void deletePattern(String id) {
        patternRepo.deleteById(id);
    }
}
