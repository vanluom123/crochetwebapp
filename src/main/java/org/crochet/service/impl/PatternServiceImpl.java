package org.crochet.service.impl;

import org.crochet.constant.AppConstant;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Category;
import org.crochet.model.Pattern;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.repository.CustomCategoryRepo;
import org.crochet.repository.CustomPatternRepo;
import org.crochet.repository.Filter;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.PatternSpecifications;
import org.crochet.repository.Specifications;
import org.crochet.service.PatternService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * PatternServiceImpl class
 */
@Service
public class PatternServiceImpl implements PatternService {
    private final PatternRepository patternRepo;
    private final CustomCategoryRepo customCategoryRepo;
    private final CustomPatternRepo customPatternRepo;

    public PatternServiceImpl(PatternRepository patternRepo,
                              CustomCategoryRepo customCategoryRepo,
                              CustomPatternRepo customPatternRepo) {
        this.patternRepo = patternRepo;
        this.customCategoryRepo = customCategoryRepo;
        this.customPatternRepo = customPatternRepo;
    }

    /**
     * Create or update pattern
     *
     * @param request PatternRequest
     */
    @Transactional
    @Override
    public PatternResponse createOrUpdate(PatternRequest request) {
        Pattern pattern;
        if (request.getId() == null) {
            var category = customCategoryRepo.findById(request.getCategoryId());
            pattern = Pattern.builder()
                    .category(category)
                    .name(request.getName())
                    .price(request.getPrice())
                    .description(request.getDescription())
                    .currencyCode(request.getCurrencyCode())
                    .isHome(request.isHome())
                    .link(request.getLink())
                    .content(request.getContent())
                    .files(FileMapper.INSTANCE.toEntities(request.getFiles()))
                    .images(FileMapper.INSTANCE.toEntities(request.getImages()))
                    .build();
        } else {
            pattern = customPatternRepo.findById(request.getId());
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
    public PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 String searchText, UUID categoryId, List<Filter> filters) {
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        Specification<Pattern> spec = Specifications.getSpecificationFromFilters(filters);
        if (searchText != null && !searchText.isEmpty()) {
            spec = spec.and(PatternSpecifications.searchByNameOrDesc(searchText));
        }
        if (categoryId != null) {
            spec = spec.and(PatternSpecifications.in(getPatternsByCategory(categoryId)));
        }
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

    @Override
    public List<PatternResponse> getLimitedPatterns() {
        var patterns = patternRepo.findAll()
                .stream()
                .filter(Pattern::isHome)
                .limit(AppConstant.PATTERN_LIMITED)
                .toList();
        return PatternMapper.INSTANCE.toResponses(patterns);
    }

    /**
     * Get pattern detail
     *
     * @param id Pattern id
     * @return PatternResponse
     */
    @Override
    public PatternResponse getDetail(UUID id) {
        var pattern = customPatternRepo.findById(id);
        return PatternMapper.INSTANCE.toResponse(pattern);
    }

    private List<Pattern> getPatternsByCategory(UUID categoryId) {
        Queue<Category> queue = new LinkedList<>();
        List<Pattern> patterns = new ArrayList<>();

        Category rootCategory = customCategoryRepo.findById(categoryId);
        queue.add(rootCategory);

        while (!queue.isEmpty()) {
            Category currentCategory = queue.poll();
            patterns.addAll(currentCategory.getPatterns());
            queue.addAll(currentCategory.getChildren());
        }
        return patterns;
    }

    @Transactional
    @Override
    public void deletePattern(UUID id) {
        patternRepo.deleteById(id);
    }
}
