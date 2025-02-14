package org.crochet.service.impl;

import com.turkraft.springfilter.converter.FilterSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.PaginationMapper;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Pattern;
import org.crochet.model.Settings;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.PatternSpecifications;
import org.crochet.service.CategoryService;
import org.crochet.service.PatternService;
import org.crochet.service.PermissionService;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * PatternServiceImpl class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatternServiceImpl implements PatternService {
    private final PatternRepository patternRepo;
    private final SettingsUtil settingsUtil;
    private final PermissionService permissionService;
    private final CategoryService categoryService;

    /**
     * Create or update pattern
     *
     * @param request PatternRequest
     */
    @Transactional
    @Override
    public void createOrUpdate(PatternRequest request) {
        Pattern pattern;
        if (!ObjectUtils.hasText(request.getId())) {
            var category = categoryService.findById(request.getCategoryId());
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
                    .files(FileMapper.INSTANCE.toEntities(files))
                    .images(FileMapper.INSTANCE.toEntities(images))
                    .build();
        } else {
            pattern = findById(request.getId());
            permissionService.checkUserPermission(pattern, "update");
            pattern = PatternMapper.INSTANCE.partialUpdate(request, pattern);
        }

        patternRepo.save(pattern);
    }

    /**
     * Get patterns
     *
     * @param offset     Page number
     * @param limit      The size of page
     * @param sortBy     Sort by
     * @param sortDir    Sort directory
     * @param categoryId The list of filters
     * @param spec       Specification<Pattern>
     * @return Pattern is paginated
     */
    @SuppressWarnings("ConstantValue")
    @Override
    public PaginationResponse<PatternResponse> getPatterns(int offset, int limit, String sortBy, String sortDir,
                                                           String categoryId, Specification<Pattern> spec) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);
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
        return PaginationMapper.getInstance().toPagination(page);
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
     * @param offset Page number
     * @param limit  Limit
     * @return List of pattern ids
     */
    @Override
    public List<String> getPatternIds(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
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
                () -> new ResourceNotFoundException(
                        ResultCode.MSG_PATTERN_NOT_FOUND.message(),
                        ResultCode.MSG_PATTERN_NOT_FOUND.code()
                ));
        return PatternMapper.INSTANCE.toResponse(pattern);
    }

    @Override
    public Pattern findById(String id) {
        return patternRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_PATTERN_NOT_FOUND.message(),
                        ResultCode.MSG_PATTERN_NOT_FOUND.code()
                ));
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
