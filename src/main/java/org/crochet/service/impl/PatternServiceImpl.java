package org.crochet.service.impl;

import org.crochet.constant.AppConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.ImageMapper;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Category;
import org.crochet.model.Pattern;
import org.crochet.model.User;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.properties.MessageCodeProperties;
import org.crochet.repository.Filter;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.PatternSpecifications;
import org.crochet.repository.Specifications;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.CategoryService;
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

import static org.crochet.constant.MessageConstant.PATTERN_NOT_FOUND_MESSAGE;
import static org.crochet.constant.MessageConstant.USER_NOT_FOUND_MESSAGE;
import static org.crochet.constant.MessageConstant.USER_NOT_PAYMENT_FOR_THIS_PATTERN_MESSAGE;

/**
 * PatternServiceImpl class
 */
@Service
public class PatternServiceImpl implements PatternService {
    private final PatternRepository patternRepo;
    private final UserRepository userRepo;
    private final CategoryService categoryService;
    private final MessageCodeProperties msgCodeProps;

    public PatternServiceImpl(PatternRepository patternRepo,
                              UserRepository userRepo,
                              CategoryService categoryService,
                              MessageCodeProperties msgCodeProps) {
        this.patternRepo = patternRepo;
        this.userRepo = userRepo;
        this.categoryService = categoryService;
        this.msgCodeProps = msgCodeProps;
    }

    /**
     * Create or update pattern
     *
     * @param request PatternRequest
     */
    @Transactional
    @Override
    public PatternResponse createOrUpdate(PatternRequest request) {
        var category = categoryService.findById(request.getCategoryId());
        var pattern = (request.getId() == null) ? new Pattern()
                : findOne(request.getId());
        pattern.setCategory(category)
                .setName(request.getName())
                .setPrice(request.getPrice())
                .setDescription(request.getDescription())
                .setCurrencyCode(request.getCurrencyCode())
                .setFiles(FileMapper.INSTANCE.toEntities(request.getFiles()))
                .setImages(ImageMapper.INSTANCE.toEntities(request.getImages()));
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
                .limit(AppConstant.PATTERN_SIZE)
                .toList();
        return PatternMapper.INSTANCE.toResponses(patterns);
    }

    /**
     * Get pattern detail
     *
     * @param principal UserPrincipal
     * @param id        Pattern id
     * @return PatternResponse
     */
    @Override
    public PatternResponse getDetail(UserPrincipal principal, String id) {
        if (principal == null) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE,
                    msgCodeProps.getCode("USER_NOT_FOUND_MESSAGE"));
        }
        User user = userRepo.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE,
                        msgCodeProps.getCode("USER_NOT_FOUND_MESSAGE")));
        var pattern = findPatternByUserOrdered(user.getId(), id);
        return PatternMapper.INSTANCE.toResponse(pattern);
    }

    private List<Pattern> getPatternsByCategory(UUID categoryId) {
        Queue<Category> queue = new LinkedList<>();
        List<Pattern> patterns = new ArrayList<>();

        Category rootCategory = categoryService.findById(categoryId);
        queue.add(rootCategory);

        while (!queue.isEmpty()) {
            Category currentCategory = queue.poll();
            patterns.addAll(currentCategory.getPatterns());
            queue.addAll(currentCategory.getChildren());
        }
        return patterns;
    }

    private Pattern findOne(String id) {
        return patternRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException(PATTERN_NOT_FOUND_MESSAGE,
                        msgCodeProps.getCode("PATTERN_NOT_FOUND_MESSAGE")));
    }

    private Pattern findPatternByUserOrdered(UUID userId, String patternId) {
        return patternRepo.findPatternByUserOrdered(userId,
                        UUID.fromString(patternId))
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_PAYMENT_FOR_THIS_PATTERN_MESSAGE,
                        msgCodeProps.getCode("USER_NOT_PAYMENT_FOR_THIS_PATTERN_MESSAGE")));
    }

    @Transactional
    @Override
    public void deletePattern(UUID id) {
        patternRepo.deleteById(id);
    }
}
