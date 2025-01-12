package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
import org.crochet.enumerator.RoleType;
import org.crochet.exception.AccessDeniedException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.FreePatternMapper;
import org.crochet.model.FreePattern;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternOnHome;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.GenericFilter;
import org.crochet.service.FreePatternService;
import org.crochet.util.ImageUtils;
import org.crochet.util.SecurityUtils;
import org.crochet.util.SettingsUtil;
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
    private final SettingsUtil settingsUtil;

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
        FreePattern freePattern;
        var sortedImages = ImageUtils.sortFiles(request.getImages());
        var sortedFiles = ImageUtils.sortFiles(request.getFiles());

        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findCategoryById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_CATEGORY_NOT_FOUND,
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
                    .files(FileMapper.INSTANCE.toSetEntities(sortedFiles))
                    .images(FileMapper.INSTANCE.toEntities(sortedImages))
                    .build();
        } else {
            freePattern = freePatternRepo.findById(request.getId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND)));
            freePattern.setName(request.getName());
            freePattern.setDescription(request.getDescription());
            freePattern.setAuthor(request.getAuthor());
            freePattern.setHome(request.isHome());
            freePattern.setLink(request.getLink());
            freePattern.setContent(request.getContent());
            freePattern.setStatus(request.getStatus());
            freePattern.setFiles(FileMapper.INSTANCE.toSetEntities(sortedFiles));
            freePattern.setImages(FileMapper.INSTANCE.toEntities(sortedImages));
        }
        freePatternRepo.save(freePattern);
        return FreePatternResponse.builder()
                .id(freePattern.getId())
                .name(freePattern.getName())
                .description(freePattern.getDescription())
                .author(freePattern.getAuthor())
                .isHome(freePattern.isHome())
                .link(freePattern.getLink())
                .content(freePattern.getContent())
                .status(freePattern.getStatus())
                .build();
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
    public PaginatedFreePatternResponse getAllFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters) {
        Specification<FreePattern> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<FreePattern> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        var freePatternIds = freePatternRepo.findAll(spec)
                .stream()
                .map(FreePattern::getId)
                .toList();

        Sort.Direction dir = Sort.Direction.fromString(sortDir);
        Sort sort = Sort.by(dir, sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        var page = freePatternRepo.getFreePatternOnHomeWithIds(freePatternIds, pageable);

        return PaginatedFreePatternResponse.builder()
                .contents(page.getContent())
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    /**
     * Get all free patterns on admin page
     *
     * @param pageNo   Page number
     * @param pageSize Page size
     * @param sortBy   Sort by
     * @param sortDir  Sort direction
     * @param filters  List filters
     * @return PaginatedFreePatternResponse
     */
    @Override
    public PaginatedFreePatternResponse getAllFreePatternsOnAdminPage(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters) {
        var currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND));
        }

        Specification<FreePattern> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<FreePattern> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        boolean isAdmin = currentUser.getRole().equals(RoleType.ADMIN);
        if (!isAdmin) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("createdBy"), currentUser.getEmail()));
        }

        var freePatternIds = freePatternRepo.findAll(spec)
                .stream()
                .map(FreePattern::getId)
                .toList();

        Sort.Direction dir = Sort.Direction.fromString(sortDir);
        Sort sort = Sort.by(dir, sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        var page = freePatternRepo.getFreePatternOnHomeWithIds(freePatternIds, pageable);

        return PaginatedFreePatternResponse.builder()
                .contents(page.getContent())
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
     * @return A list of {@link FreePatternResponse} objects containing information
     * about the FreePatterns.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FreePatternOnHome> getLimitedFreePatterns() {
        var settingsMap = settingsUtil.getSettingsMap();
        if (settingsMap.isEmpty()) {
            return Collections.emptyList();
        }

        var direction = settingsMap.get("homepage.fp.direction").getValue();

        var orderBy = settingsMap.get("homepage.fp.orderBy").getValue();

        var limit = settingsMap.get("homepage.fp.limit").getValue();

        Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable pageable = PageRequest.of(0, Integer.parseInt(limit), sort);

        return freePatternRepo.findLimitedNumFreePattern(pageable);
    }

    /**
     * Get free pattern ids
     *
     * @param pageNo Page number
     * @param limit  Limit
     * @return List of free pattern ids
     */
    @Override
    public List<String> getFreePatternIds(int pageNo, int limit) {
        Pageable pageable = PageRequest.of(pageNo, limit);
        return freePatternRepo.getFreePatternIds(pageable);
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
        var freePattern = freePatternRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND)));
        return FreePatternMapper.INSTANCE.toResponse(freePattern);
    }

    @Transactional
    @Override
    public void delete(String id) {
        var currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND));
        }

        var freePattern = freePatternRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND)));

        boolean isAdmin = currentUser.getRole().equals(RoleType.ADMIN);
        if (!isAdmin && !freePattern.getCreatedBy().equals(currentUser.getEmail())) {
            throw new AccessDeniedException(MessageConstant.MSG_FORBIDDEN,
                    MAP_CODE.get(MessageConstant.MSG_FORBIDDEN));
        }

        freePatternRepo.delete(freePattern);
    }

    @Transactional
    @Override
    public void deleteAllById(List<String> ids) {
        var currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND));
        }

        // Delete all free patterns if user is admin. Otherwise, delete only free patterns created by the user
        if (currentUser.getRole().equals(RoleType.ADMIN)) {
            freePatternRepo.deleteAllById(ids);
        } else {
            freePatternRepo.deleteAllByIdAndCreatedBy(ids, currentUser.getEmail());
        }
    }

    @Override
    public List<FreePatternOnHome> getFrepsByCreateBy() {
        var currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                    MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND));
        }
        return freePatternRepo.getFrepsByCreateBy(currentUser.getEmail());
    }
}
