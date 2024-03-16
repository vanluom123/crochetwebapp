package org.crochet.service.impl;

import org.crochet.constant.AppConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.ImageMapper;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.File;
import org.crochet.model.Image;
import org.crochet.model.Pattern;
import org.crochet.model.User;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.PatternSpecifications;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.CategoryService;
import org.crochet.service.PatternService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.crochet.constant.MessageCode.*;
import static org.crochet.constant.MessageConstant.*;

/**
 * PatternServiceImpl class
 */
@Service
public class PatternServiceImpl implements PatternService {
    private final PatternRepository patternRepo;
    private final UserRepository userRepo;
    private final CategoryService categoryService;

    public PatternServiceImpl(PatternRepository patternRepo,
                              UserRepository userRepo,
                              CategoryService categoryService) {
        this.patternRepo = patternRepo;
        this.userRepo = userRepo;
        this.categoryService = categoryService;
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
        pattern.setCategory(category);
        pattern.setName(request.getName());
        pattern.setPrice(request.getPrice());
        pattern.setDescription(request.getDescription());
        pattern.setCurrencyCode(request.getCurrencyCode());

        Set<File> files = FileMapper.INSTANCE.toEntities(request.getFiles());
        for (var file : files) {
            file.setPattern(pattern);
        }
        pattern.setFiles(files);


        Set<Image> images = ImageMapper.INSTANCE.toEntities(request.getImages());
        for (var image : images) {
            image.setPattern(pattern);
        }
        pattern.setImages(images);

        pattern = patternRepo.save(pattern);
        return PatternMapper.INSTANCE.toResponse(pattern);
    }

    /**
     * Get patterns
     *
     * @param pageNo      Page number
     * @param pageSize    The size of page
     * @param sortBy      Sort by
     * @param sortDir     Sort directory
     * @param text        Text
     * @param categoryIds
     * @return Pattern is paginated
     */
    @Override
    public PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text, List<UUID> categoryIds) {
        // create Sort instance
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Pattern> spec = Specification.where(null);
        if (text != null && !text.isEmpty()) {
            spec = spec.and(PatternSpecifications.searchBy(text));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and(PatternSpecifications.filterBy(categoryIds));
        }

        Page<Pattern> menuPage = patternRepo.findAll(spec, pageable);
        List<PatternResponse> responses = PatternMapper.INSTANCE.toResponses(menuPage.getContent());

        return PatternPaginationResponse.builder()
                .contents(responses)
                .pageNo(menuPage.getNumber())
                .pageSize(menuPage.getSize())
                .totalElements(menuPage.getTotalElements())
                .totalPages(menuPage.getTotalPages())
                .last(menuPage.isLast())
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
     * @param id Id
     * @return Pattern response
     */
    @Override
    public PatternResponse getDetail(String id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException(USER_NOT_LOGGED_IN_MESSAGE, USER_NOT_LOGGED_IN_CODE);
        }
        User user = userRepo.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE, USER_NOT_FOUND_CODE));
        var pattern = findPatternByUserOrdered(user.getId(), id);
        return PatternMapper.INSTANCE.toResponse(pattern);
    }

    private Pattern findOne(String id) {
        return patternRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException(PATTERN_NOT_FOUND_MESSAGE, PATTERN_NOT_FOUND_CODE));
    }

    private Pattern findPatternByUserOrdered(UUID userId, String patternId) {
        return patternRepo.findPatternByUserOrdered(userId,
                        UUID.fromString(patternId))
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_PAYMENT_FOR_THIS_PATTERN_MESSAGE, USER_NOT_PAYMENT_FOR_THIS_PATTERN_CODE));
    }

    @Transactional
    @Override
    public void deletePattern(UUID id) {
        patternRepo.deleteById(id);
    }
}
