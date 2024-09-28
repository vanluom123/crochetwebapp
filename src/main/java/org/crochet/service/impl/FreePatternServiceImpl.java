package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.FreePatternMapper;
import org.crochet.model.FreePattern;
import org.crochet.model.SavingChart;
import org.crochet.model.User;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.GenericFilter;
import org.crochet.repository.SavingChartRepo;
import org.crochet.repository.SettingsRepo;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.FreePatternService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;
import static org.crochet.constant.MessageConstant.MSG_USER_LOGIN_REQUIRED;
import static org.crochet.constant.MessageConstant.MSG_USER_NOT_FOUND;

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
    private final SavingChartRepo savingChartRepo;

    /**
     * Creates a new FreePattern or updates an existing one based on the provided {@link FreePatternRequest}.
     * If the request contains an ID, it updates the existing FreePattern with the corresponding ID.
     * If the request does not contain an ID, it creates a new FreePattern.
     *
     * @param request The {@link FreePatternRequest} containing information for creating or updating the FreePattern.
     * @return FreePatternResponse
     */
    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "limitedfreepatterns", allEntries = true)
            }
    )
    public FreePatternResponse createOrUpdate(FreePatternRequest request) {
        FreePattern freePattern;
        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_CATEGORY_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_CATEGORY_NOT_FOUND))
            );
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
                            MAP_CODE.get(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND))
            );
            freePattern = FreePatternMapper.INSTANCE.partialUpdate(request, freePattern);
        }
        freePattern = freePatternRepo.save(freePattern);
        return FreePatternMapper.INSTANCE.toResponse(freePattern);
    }

    /**
     * Get all free patterns with filter
     *
     * @param pageNo    Page number
     * @param pageSize  Page size
     * @param sortBy    Sort by
     * @param sortDir   Sort direction
     * @param filters   List Filters
     * @param principal UserPrincipal
     * @return PaginatedFreePatternResponse
     */
    @Override
    public PaginatedFreePatternResponse getAllFreePatterns(int pageNo,
                                                           int pageSize,
                                                           String sortBy,
                                                           Sort.Direction sortDir,
                                                           Filter[] filters,
                                                           UserPrincipal principal) {
        Specification<FreePattern> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<FreePattern> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        Sort sort = Sort.by(sortDir, sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<FreePattern> page = freePatternRepo.findAll(spec, pageable);

        List<FreePatternResponse> contents = page.getContent().stream()
                .map(pattern -> mapToFreePatternResponse(pattern, principal))
                .toList();

        return PaginatedFreePatternResponse.builder()
                .contents(contents)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    /**
     * Retrieves a limited list of FreePatterns.
     *
     * @return A list of {@link FreePatternResponse} objects containing information about the FreePatterns.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Cacheable(value = "limitedfreepatterns")
    @Override
    public List<FreePatternResponse> getLimitedFreePatterns() {
        log.info("Fetching limited free patterns");
        var direction = settingsRepo.findByKey("homepage.fp.direction")
                .orElse(Sort.Direction.ASC.name());
        var orderBy = settingsRepo.findByKey("homepage.fp.orderBy")
                .orElse("id");
        var limit = settingsRepo.findByKey("homepage.fp.limit")
                .orElse(AppConstant.DEFAULT_LIMIT);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable pageable = PageRequest.of(0, Integer.parseInt(limit), sort);
        var freePatterns = freePatternRepo.findLimitedNumFreePattern(pageable);
        return FreePatternMapper.INSTANCE.toResponses(freePatterns);
    }

    /**
     * Retrieves detailed information for a specific FreePattern identified by the given ID.
     *
     * @param id The unique identifier of the FreePattern.
     * @return A {@link FreePatternResponse} containing detailed information about the FreePattern.
     */
    @Override
    public FreePatternResponse getDetail(String id) {
        var freePattern = freePatternRepo.getDetail(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND))
        );
        return FreePatternMapper.INSTANCE.toResponse(freePattern);
    }

    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "limitedfreepatterns", allEntries = true)
            }
    )
    public void delete(String id) {
        freePatternRepo.deleteById(id);
    }

    /**
     * Retrieves a paginated response of all saved patterns for a specific user, based on provided pagination
     * and sorting parameters, and optional filters.
     *
     * @param pageNo The page number to retrieve.
     * @param pageSize The number of records per page.
     * @param sortBy The attribute to sort the records by.
     * @param sortDir The direction of the sort, either "asc" or "desc".
     * @param filters An array of filters to apply to the search.
     * @param principal The authentication principal representing the user.
     * @return A paginated response containing the saved patterns for the user.
     * @throws ResourceNotFoundException If the user is not authenticated or if the user is not found.
     */
    @Override
    public PaginatedFreePatternResponse getAllSavedPatternByUser(int pageNo,
                                                                 int pageSize,
                                                                 String sortBy,
                                                                 Sort.Direction sortDir,
                                                                 Filter[] filters,
                                                                 UserPrincipal principal) {
        if (principal == null) {
            throw new ResourceNotFoundException(MSG_USER_LOGIN_REQUIRED, MAP_CODE.get(MSG_USER_LOGIN_REQUIRED));
        }
        User user = userRepo.findById(principal.getId()).orElseThrow(
                () -> new ResourceNotFoundException(MSG_USER_NOT_FOUND, MAP_CODE.get(MSG_USER_NOT_FOUND))
        );

        Specification<SavingChart> spec = Specification.where((root, query, cb) -> cb.equal(root.get("user"), user));
        if (filters != null && filters.length > 0) {
            GenericFilter<SavingChart> filter = GenericFilter.create(filters);
            spec = spec.and(filter.build());
        }

        Sort sort = Sort.by(sortDir, sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<SavingChart> page = savingChartRepo.findAll(spec, pageable);

        List<FreePatternResponse> contents = page.getContent().stream()
                .map(savingChart -> FreePatternMapper.INSTANCE.toResponse(savingChart.getFreePattern()))
                .toList();

        return PaginatedFreePatternResponse.builder()
                .contents(contents)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    private FreePatternResponse mapToFreePatternResponse(FreePattern pattern, UserPrincipal userPrincipal) {
        FreePatternResponse response = FreePatternMapper.INSTANCE.toResponse(pattern);

        if (userPrincipal != null) {
            boolean isSaved = savingChartRepo.existsByUserIdAndFreePatternId(userPrincipal.getId(), pattern.getId());
            response.setSaved(isSaved);
        } else {
            response.setSaved(false);
        }

        return response;
    }
}
