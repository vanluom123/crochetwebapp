package org.crochet.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.FreePatternMapper;
import org.crochet.model.Category;
import org.crochet.model.FreePattern;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.repository.CustomCategoryRepo;
import org.crochet.repository.CustomFreePatternRepo;
import org.crochet.repository.Filter;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.FreePatternSpecifications;
import org.crochet.repository.Specifications;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * FreePatternServiceImpl class
 */
@Slf4j
@Service
public class FreePatternServiceImpl implements FreePatternService {
    private final FreePatternRepository freePatternRepo;
    private final CustomCategoryRepo customCategoryRepo;
    private final CustomFreePatternRepo customFreePatternRepo;

    /**
     * Constructs a new {@code FreePatternServiceImpl} with the specified FreePattern repository.
     *
     * @param freePatternRepo       The repository for handling FreePattern-related operations.
     * @param customCategoryRepo    The repository for handling CustomCategory-related operations.
     * @param customFreePatternRepo The repository for handling CustomFreePattern-related operations.
     */
    public FreePatternServiceImpl(FreePatternRepository freePatternRepo,
                                  CustomCategoryRepo customCategoryRepo,
                                  CustomFreePatternRepo customFreePatternRepo) {
        this.freePatternRepo = freePatternRepo;
        this.customCategoryRepo = customCategoryRepo;
        this.customFreePatternRepo = customFreePatternRepo;
    }

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
            evict = {@CacheEvict(value = "limitedfreepatterns", allEntries = true)}
    )
    public FreePatternResponse createOrUpdate(FreePatternRequest request) {
        FreePattern freePattern;
        if (!StringUtils.hasText(request.getId())) {
            var category = customCategoryRepo.findById(request.getCategoryId());
            freePattern = FreePattern.builder()
                    .category(category)
                    .name(request.getName())
                    .description(request.getDescription())
                    .author(request.getAuthor())
                    .isHome(request.isHome())
                    .link(request.getLink())
                    .content(request.getContent())
                    .files(FileMapper.INSTANCE.toEntities(request.getFiles()))
                    .images(FileMapper.INSTANCE.toEntities(request.getImages()))
                    .build();
        } else {
            freePattern = customFreePatternRepo.findById(request.getId());
            freePattern = FreePatternMapper.INSTANCE.partialUpdate(request, freePattern);
            if (!Objects.equals(freePattern.getCategory().getId(), request.getCategoryId())) {
                var category = customCategoryRepo.findById(request.getCategoryId());
                freePattern.setCategory(category);
            }
        }
        freePattern = freePatternRepo.save(freePattern);
        return FreePatternMapper.INSTANCE.toResponse(freePattern);
    }

    /**
     * Retrieves a paginated list of FreePatterns based on the provided parameters.
     *
     * @param pageNo     The page number to retrieve (0-indexed).
     * @param pageSize   The number of FreePatterns to include in each page.
     * @param sortBy     The attribute by which the FreePatterns should be sorted.
     * @param sortDir    The sorting direction, either "ASC" (ascending) or "DESC" (descending).
     * @param searchText The text to search for in the FreePatterns.
     * @param categoryId The unique identifier of the category to filter FreePatterns by.
     * @param filters    The list of filters.
     * @return A {@link PaginatedFreePatternResponse} containing the paginated list of FreePatterns.
     */
    @Override
    public PaginatedFreePatternResponse getFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir,
                                                        String searchText, String categoryId, List<Filter> filters) {
        // create Sort instance
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Specification<FreePattern> spec = Specifications.getSpecificationFromFilters(filters);
        if (StringUtils.hasText(searchText)) {
            spec = spec.or(FreePatternSpecifications.searchByNameDescOrAuthor(searchText));
        }
        // add filter criteria
        if (StringUtils.hasText(categoryId)) {
            spec = spec.or(FreePatternSpecifications.existIn(getAllFreePatterns(categoryId)));
        }
        // retrieve FreePatterns
        Page<FreePattern> page = freePatternRepo.findAll(spec, pageable);
        List<FreePatternResponse> contents = FreePatternMapper.INSTANCE.toResponses(page.getContent());
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
     * Retrieves a list of FreePatterns based on the provided category ID using a recursive approach.
     * This method traverses the category hierarchy by recursively calling itself for each child category.
     *
     * @param categoryId The unique identifier of the category.
     * @return A list of {@link FreePattern} objects containing information about the FreePatterns.
     */
    private List<FreePattern> getAllFreePatterns(String categoryId) {
        Category category = customCategoryRepo.findById(categoryId);
        List<FreePattern> freePatterns = new ArrayList<>(category.getFreePatterns());
        for (Category subCategory : category.getChildren()) {
            freePatterns.addAll(getAllFreePatterns(subCategory.getId()));
        }
        return freePatterns;
    }

    /**
     * Retrieves a limited list of FreePatterns.
     * The limit is defined by the constant {@code AppConstant.FREE_PATTERN_SIZE}.
     *
     * @return A list of {@link FreePatternResponse} objects containing information about the FreePatterns.
     */
    @Cacheable(value = "limitedfreepatterns")
    @Override
    public List<FreePatternResponse> getLimitedFreePatterns() {
        log.info("Fetching limited free patterns");
        var freePatterns = freePatternRepo.findAll()
                .stream()
                .filter(FreePattern::isHome)
                .limit(AppConstant.FREE_PATTERN_LIMITED)
                .toList();
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
        var freePattern = customFreePatternRepo.findById(id);
        return FreePatternMapper.INSTANCE.toResponse(freePattern);
    }

    @Transactional
    @Override
    @CacheEvict(value = "limitedfreepatterns", allEntries = true)
    public void delete(String id) {
        customFreePatternRepo.deleteById(id);
    }
}
