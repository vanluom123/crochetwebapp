package org.crochet.service.impl;

import com.turkraft.springfilter.converter.FilterSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Pattern;
import org.crochet.model.Settings;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.PatternSpecifications;
import org.crochet.service.PatternService;
import org.crochet.util.ImageUtils;
import org.crochet.util.ObjectUtils;
import org.crochet.util.SettingsUtil;
import org.springframework.data.domain.Page;
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
    private final SettingsUtil settingsUtil;

    /**
     * Create or update pattern
     *
     * @param request PatternRequest
     */
    @Transactional
    @Override
    public void createOrUpdate(PatternRequest request) {
        Pattern pattern;

        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_CATEGORY_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_CATEGORY_NOT_FOUND)));
            var images = ImageUtils.sortFiles(request.getImages());
            var files = ImageUtils.sortFiles(request.getFiles());
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
            pattern = PatternMapper.INSTANCE.partialUpdate(request, pattern);
        }

        patternRepo.save(pattern);
    }

    /**
     * Get patterns
     *
     * @param pageNo     Page number
     * @param pageSize   The size of page
     * @param sortBy     Sort by
     * @param sortDir    Sort directory
     * @param categoryId The list of filters
     * @param spec       Specification<Pattern>
     * @return Pattern is paginated
     */
    @SuppressWarnings("ConstantValue")
    @Override
    public PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 String categoryId, Specification<Pattern> spec) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<PatternResponse> page;
        var filter = ((FilterSpecification<Pattern>) spec).getFilter();
        var hasCategory = ObjectUtils.hasText(categoryId);
        if (hasCategory) {
            spec = spec.and(PatternSpecifications.inHierarchy(categoryId));
        }
        if ((filter != null && ObjectUtils.isNotEmpty(filter.getChildren())) || hasCategory) {
            var patternIds = patternRepo.findAll(spec)
                    .stream()
                    .map(Pattern::getId)
                    .toList();
            page = patternRepo.findPatternWithIds(patternIds, pageable);
        } else {
            page = patternRepo.findPatternWithPageable(pageable);

        }
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
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Override
    public List<PatternResponse> getLimitedPatterns() {
        Map<String, Settings> settingsMap = settingsUtil.getSettingsMap();
        if (settingsMap.isEmpty()) {
            return Collections.emptyList();
        }

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
    @Transactional(readOnly = true)
    @Override
    public PatternResponse getDetail(String id) {
        var pattern = patternRepo.findPatternById(id).orElseThrow(
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
