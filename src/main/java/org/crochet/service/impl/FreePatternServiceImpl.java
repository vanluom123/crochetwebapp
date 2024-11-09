package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.crochet.constant.MessageConstant;
import org.crochet.enumerator.RoleType;
import org.crochet.exception.AccessDeniedException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.FreePatternMapper;
import org.crochet.model.FreePattern;
import org.crochet.model.User;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.GenericFilter;
import org.crochet.repository.SettingsRepo;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.CacheService;
import org.crochet.service.FreePatternService;
import org.crochet.service.ResilientCacheService;
import org.springframework.data.domain.Page;
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
 * FreePatternServiceImpl class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FreePatternServiceImpl implements FreePatternService {
    private final FreePatternRepository freePatternRepo;
    private final CategoryRepo categoryRepo;
    private final SettingsRepo settingsRepo;
    private final UserRepository userRepo;
    private final CacheService cacheService;
    private final ResilientCacheService resilientCacheService;

    /**
     * Creates a new FreePattern or updates an existing one based on the provided
     * {@link FreePatternRequest}.
     * If the request contains an ID, it updates the existing FreePattern with the
     * corresponding ID.
     * If the request does not contain an ID, it creates a new FreePattern.
     *
     * @param request The {@link FreePatternRequest} containing information for
     *                creating or updating the FreePattern.
     * @return FreePatternResponse
     */
    @Transactional
    @Override
    public FreePatternResponse createOrUpdate(FreePatternRequest request) {
        cacheService.invalidateCache("fp_*");
        FreePattern freePattern;
        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_CATEGORY_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_CATEGORY_NOT_FOUND)));
            freePattern = FreePattern.builder()
                    .category(category)
                    .name(request.getName())
                    .description(request.getDescription())
                    .author(request.getAuthor())
                    .isHome(request.isHome())
                    .link(request.getLink())
                    .content(request.getContent())
                    .status(request.getStatus())
                    .files(FileMapper.INSTANCE.toSetEntities(request.getFiles()))
                    .images(FileMapper.INSTANCE.toEntities(request.getImages()))
                    .build();
        } else {
            freePattern = freePatternRepo.findById(request.getId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND)));
            freePattern = FreePatternMapper.INSTANCE.partialUpdate(request, freePattern);
        }
        freePattern = freePatternRepo.save(freePattern);
        return FreePatternMapper.INSTANCE.toResponse(freePattern);
    }

    /**
     * Get all free patterns with filter
     *
     * @param pageNo   Page number
     * @param pageSize Page size
     * @param sortBy   Sort by
     * @param sortDir  Sort direction
     * @param filters  List Filters
     * @return PaginatedFreePatternResponse
     */
    @Override
    public PaginatedFreePatternResponse getAllFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir,
            Filter[] filters) {

        if (pageNo < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid page number or page size");
        }

        if (sortBy == null || sortBy.isBlank()) {
            throw new IllegalArgumentException("Sort field cannot be null or empty");
        }

        String cacheKey = String.format("fp_pageNo%d_pageSize%d_sortBy%s_sortDir%s", pageNo, pageSize, sortBy, sortDir);
        if (cacheService.hasKey(cacheKey)) {
            log.info("Returning cached response for key: {}", cacheKey);
            var response = resilientCacheService.getCachedResult(cacheKey, PaginatedFreePatternResponse.class);
            if (response.isPresent()) {
                return response.get();
            }
        }

        log.info("No cached response found for key: {}", cacheKey);

        Specification<FreePattern> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<FreePattern> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<FreePattern> page = freePatternRepo.findAll(spec, pageable);
        List<FreePatternResponse> contents = FreePatternMapper.INSTANCE.toResponses(page.getContent());

        var response = PaginatedFreePatternResponse.builder()
                .contents(contents)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();

        cacheService.set(cacheKey, response, Duration.ofDays(1));
        return response;
    }

    /**
     * Get all free patterns on admin page
     *
     * @param currentUser Current user
     * @param pageNo      Page number
     * @param pageSize    Page size
     * @param sortBy      Sort by
     * @param sortDir     Sort direction
     * @param filters     List filters
     * @return PaginatedFreePatternResponse
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public PaginatedFreePatternResponse getAllFreePatternsOnAdminPage(UserPrincipal currentUser,
            int pageNo,
            int pageSize,
            String sortBy,
            String sortDir,
            Filter[] filters) {
    
        if (currentUser == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND));
        }

        if (pageNo < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid page number or page size");
        }

        if (sortBy == null || sortBy.isBlank()) {
            throw new IllegalArgumentException("Sort field cannot be null or empty");
        }

        String cacheKey = String.format("fp_admin_pageNo%d_pageSize%d_sortBy%s_sortDir%s", pageNo, pageSize, sortBy, sortDir);
        if (cacheService.hasKey(cacheKey)) {
            var response = cacheService.get(cacheKey, PaginatedFreePatternResponse.class);
            if (response.isPresent()) {
                return response.get();
            }
        }

        Specification<FreePattern> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<FreePattern> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        User user = userRepo.findById(currentUser.getId()).get();

        boolean isAdmin = user.getRole().equals(RoleType.ADMIN);
        if (!isAdmin) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("createdBy"), user.getEmail()));
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Page<FreePattern> page = freePatternRepo.findAll(spec,
                PageRequest.of(pageNo, pageSize, sort));

        List<FreePatternResponse> contents = FreePatternMapper.INSTANCE.toResponses(page.getContent());
        var response = PaginatedFreePatternResponse.builder()
                .contents(contents)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();

        cacheService.set(cacheKey, response, Duration.ofDays(1));
        
        return response;
    }

    /**
     * Retrieves a limited list of FreePatterns.
     *
     * @return A list of {@link FreePatternResponse} objects containing information
     *         about the FreePatterns.
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FreePatternResponse> getLimitedFreePatterns() {
        String cacheKey = "fp_limited";
        
        if (cacheService.hasKey(cacheKey)) {
            var response = cacheService.get(cacheKey, List.class);
            if (response.isPresent()) {
                return response.get();
            }
        }

        var direction = settingsRepo.findByKey("homepage.fp.direction")
                .orElse(Sort.Direction.ASC.name());

        var orderBy = settingsRepo.findByKey("homepage.fp.orderBy")
                .orElse("id");

        var limit = settingsRepo.findByKey("homepage.fp.limit")
                .orElse(AppConstant.DEFAULT_LIMIT);

        Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable pageable = PageRequest.of(0, Integer.parseInt(limit), sort);
        var freePatterns = freePatternRepo.findLimitedNumFreePattern(pageable);
        var response = FreePatternMapper.INSTANCE.toResponses(freePatterns);

        cacheService.set(cacheKey, response, Duration.ofDays(1));

        return response;
    }

    /**
     * Retrieves detailed information for a specific FreePattern identified by the
     * given ID.
     *
     * @param id The unique identifier of the FreePattern.
     * @return A {@link FreePatternResponse} containing detailed information about
     * the FreePattern.
     */
    @Override
    public FreePatternResponse getDetail(String id) {
        var freePattern = freePatternRepo.getDetail(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND)));
        return FreePatternMapper.INSTANCE.toResponse(freePattern);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Transactional
    @Override
    public void delete(UserPrincipal currentUser, String id) {
        if (currentUser == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND));
        }

        cacheService.invalidateCache("fp_*");

        var user = userRepo.findById(currentUser.getId()).get();

        var freePattern = freePatternRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND)));

        boolean isAdmin = user.getRole().equals(RoleType.ADMIN);
        if (!isAdmin && !freePattern.getCreatedBy().equals(currentUser.getEmail())) {
            throw new AccessDeniedException(MessageConstant.MSG_FORBIDDEN,
                    MAP_CODE.get(MessageConstant.MSG_FORBIDDEN));
        }

        freePatternRepo.delete(freePattern);
    }
}
