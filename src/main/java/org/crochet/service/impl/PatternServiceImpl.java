package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Pattern;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternDetailResponse;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.Filter;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.PatternSpecifications;
import org.crochet.repository.Specifications;
import org.crochet.service.PatternService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * PatternServiceImpl class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatternServiceImpl implements PatternService {
    final PatternRepository patternRepo;
    final CategoryRepo categoryRepo;

    /**
     * Create or update pattern
     *
     * @param request PatternRequest
     */
    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "limitedpatterns", allEntries = true),
                    @CacheEvict(value = "patterns", allEntries = true)
            }
    )
    public PatternResponse createOrUpdate(PatternRequest request) {
        Pattern pattern;
        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                    () -> new ResourceNotFoundException("Category not found")
            );
            pattern = Pattern.builder()
                    .category(category)
                    .name(request.getName())
                    .price(request.getPrice())
                    .description(request.getDescription())
                    .currencyCode(request.getCurrencyCode())
                    .isHome(request.isHome())
                    .link(request.getLink())
                    .content(request.getContent())
                    .files(new HashSet<>(FileMapper.INSTANCE.toEntities(request.getFiles())))
                    .images(new HashSet<>(FileMapper.INSTANCE.toEntities(request.getImages())))
                    .build();
        } else {
            pattern = patternRepo.findById(request.getId()).orElseThrow(
                    () -> new ResourceNotFoundException("Pattern not found")
            );
            pattern = PatternMapper.INSTANCE.partialUpdate(request, pattern);
        }
        pattern = patternRepo.save(pattern);
        return PatternMapper.INSTANCE.toResponse(pattern);
    }

    /**
     * Get patterns
     *
     * @param pageNo     Page number
     * @param pageSize   The size of page
     * @param sortBy     Sort by
     * @param sortDir    Sort directory
     * @param searchText Search text
     * @param categoryId Category id
     * @param filters    Filters
     * @return Pattern is paginated
     */
    @Override
    @Cacheable(value = "patterns", key = "T(java.util.Objects).hash(#pageNo, #pageSize, #sortBy, #sortDir, #searchText, #categoryId, #filters)")
    public PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 String searchText, String categoryId, List<Filter> filters) {
        log.info("Fetching patterns");
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();

        Specification<Pattern> spec = Specifications.getSpecFromFilters(filters);

        if (StringUtils.hasText(searchText)) {
            spec = spec.or(PatternSpecifications.searchByNameOrDesc(searchText));
        }
        if (StringUtils.hasText(categoryId)) {
            spec = spec.or(PatternSpecifications.in(getPatternsByCategory(categoryId)));
        }

        spec = spec.and(PatternSpecifications.getAll());

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        var page = patternRepo.findAll(spec, pageable);

        List<PatternResponse> responses = PatternMapper.INSTANCE.toResponses(page.getContent());
        return PatternPaginationResponse.builder()
                .contents(responses)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Cacheable(value = "limitedpatterns")
    @Override
    public List<PatternResponse> getLimitedPatterns() {
        log.info("Fetching limited patterns");
        var patterns = patternRepo.findLimitedNumPatternByCreatedDateDesc();
        return PatternMapper.INSTANCE.toResponses(patterns);
    }

    /**
     * Get pattern detail
     *
     * @param id Pattern id
     * @return PatternResponse
     */
    @Override
    public PatternDetailResponse getDetail(String id) {
        var pattern = patternRepo.getDetail(id).orElseThrow(
                () -> new ResourceNotFoundException("Pattern not found")
        );
        return PatternMapper.INSTANCE.toPatternDetailResponse(pattern);
    }

    /**
     * Get patterns by category
     *
     * @param categoryId Category id
     * @return List of patterns
     */
    private List<Pattern> getPatternsByCategory(String categoryId) {
        Queue<String> queue = new LinkedList<>();
        List<Pattern> patterns = new ArrayList<>();

        queue.add(categoryId);

        while (!queue.isEmpty()) {
            var childId = queue.poll();
            patterns.addAll(patternRepo.findPatternByCategory(childId));
            queue.addAll(categoryRepo.findChildrenIds(childId));
        }
        return patterns;
    }

    /**
     * Delete pattern
     *
     * @param id Pattern id
     */
    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "limitedpatterns", allEntries = true),
                    @CacheEvict(value = "patterns", allEntries = true)
            }
    )
    public void deletePattern(String id) {
        patternRepo.deleteById(id);
    }
}
