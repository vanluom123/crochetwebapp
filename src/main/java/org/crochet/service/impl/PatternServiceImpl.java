package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Pattern;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.GenericFilter;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.SettingsRepo;
import org.crochet.service.CacheService;
import org.crochet.service.PatternService;
import org.crochet.service.ResilientCacheService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;

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
    private final CacheService cacheService;
    private final ResilientCacheService resilientCacheService;

    /**
     * Create or update pattern
     *
     * @param request PatternRequest
     */
    @Transactional
    @Override
    public PatternResponse createOrUpdate(PatternRequest request) {
        cacheService.invalidateCache("pattern_*");
        Pattern pattern;
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
                    .files(FileMapper.INSTANCE.toSetEntities(request.getFiles()))
                    .images(FileMapper.INSTANCE.toEntities(request.getImages()))
                    .build();
        } else {
            pattern = patternRepo.findById(request.getId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_PATTERN_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_PATTERN_NOT_FOUND)));
            pattern = PatternMapper.INSTANCE.partialUpdate(request, pattern);
        }
        pattern = patternRepo.save(pattern);
        return PatternMapper.INSTANCE.toResponse(pattern);
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

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        var page = patternRepo.findAll(spec, pageable);

        List<PatternResponse> responses = PatternMapper.INSTANCE.toResponses(page.getContent());

        var response = PatternPaginationResponse.builder()
                .contents(responses)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();

        return response;
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<PatternResponse> getLimitedPatterns() {
        String cacheKey = "pattern_limited";
        if (cacheService.hasKey(cacheKey)) {
            var response = resilientCacheService.getCachedResult(cacheKey, List.class);
            if (response.isPresent()) {
                log.debug("Returning cached patterns");
                return response.get();
            }
        }

        var direction = settingsRepo.findByKey("homepage.pattern.direction")
                .orElse(Sort.Direction.ASC.name());

        var orderBy = settingsRepo.findByKey("homepage.pattern.orderBy")
                .orElse("id");

        var limit = settingsRepo.findByKey("homepage.pattern.limit")
                .orElse(AppConstant.DEFAULT_LIMIT);

        Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable pageable = PageRequest.of(0, Integer.parseInt(limit), sort);
        var patterns = patternRepo.findLimitedNumPattern(pageable);

        cacheService.set(cacheKey, patterns, Duration.ofDays(1));

        return PatternMapper.INSTANCE.toResponses(patterns);
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
        cacheService.invalidateCache("pattern_*");
        patternRepo.deleteById(id);
    }
}
