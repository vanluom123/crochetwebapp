package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
import org.crochet.enumerator.RoleType;
import org.crochet.exception.AccessDeniedException;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CategoryMapper;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.FreePatternMapper;
import org.crochet.model.FreePattern;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.FreePatternRepoCustom;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.GenericFilter;
import org.crochet.repository.UserRepository;
import org.crochet.service.FreePatternService;
import org.crochet.util.ImageUtils;
import org.crochet.util.SecurityUtils;
import org.crochet.util.SettingsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
    private final UserRepository userRepo;
    private final FreePatternRepoCustom freePatternRepoCustom;

    /**
     * Creates a new FreePattern or updates an existing one based on the provided
     * {@link FreePatternRequest}.
     * If the request contains an ID, it updates the existing FreePattern with the
     * corresponding ID.
     * If the request does not contain an ID, it creates a new FreePattern.
     *
     * @param request The {@link FreePatternRequest} containing information for
     *                creating or updating the FreePattern.
     */
    @Transactional
    @Override
    public void createOrUpdate(FreePatternRequest request) {
        FreePattern freePattern;
        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findCategoryById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_CATEGORY_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_CATEGORY_NOT_FOUND)));
            var sortedImages = ImageUtils.sortFiles(request.getImages());
            var sortedFiles = ImageUtils.sortFiles(request.getFiles());
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
            freePattern = FreePatternMapper.INSTANCE.update(request, freePattern);
        }
        freePatternRepo.save(freePattern);
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
    @Transactional(readOnly = true)
    @Override
    public PaginatedFreePatternResponse getAllFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters) {
        List<String> freePatternIds = new ArrayList<>();
        var pageable = preparePageableAndFilter(pageNo, pageSize, sortBy, sortDir, filters, freePatternIds);

        Page<FreePatternResponse> page;
        if (freePatternIds.isEmpty()) {
            page = freePatternRepo.getFrepWithPageable(pageable);
        } else {
            page = freePatternRepo.getFrepByIds(freePatternIds, pageable);
        }

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
     * Retrieves a paginated and sorted list of free patterns associated with a specific user,
     * optionally filtered by specified criteria.
     *
     * @param pageNo   the page number to retrieve (zero-based)
     * @param pageSize the number of items per page
     * @param sortBy   the attribute to sort the results by
     * @param sortDir  the direction to sort the results (e.g., "asc" or "desc")
     * @param filters  an array of filter conditions to apply to the query, can be null or empty
     * @param userId   the ID of the user whose free patterns are to be retrieved
     * @return a {@link PaginatedFreePatternResponse} containing the paginated and filtered list of free patterns
     */
    @Transactional(readOnly = true)
    @Override
    public PaginatedFreePatternResponse getAllByUser(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters, String userId) {
        List<String> freePatternIds = new ArrayList<>();
        var pageable = preparePageableAndFilter(pageNo, pageSize, sortBy, sortDir, filters, freePatternIds);
        Page<FreePatternResponse> page;
        if(freePatternIds.isEmpty()) {
            page = freePatternRepo.getByUserWithPageable(userId, pageable);
        } else {
            page = freePatternRepo.getByUserAndIds(userId, freePatternIds, pageable);
        }

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
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Override
    public List<FreePatternResponse> getLimitedFreePatterns() {
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
    @Transactional(readOnly = true)
    @Override
    public FreePatternResponse getDetail(String id) {
        var frep = freePatternRepo.findFrepById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_FREE_PATTERN_NOT_FOUND)));
        var user = userRepo.findById(frep.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_USER_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_USER_NOT_FOUND)));
        var images = FileMapper.INSTANCE.toResponses(frep.getImages());
        var files = FileMapper.INSTANCE.toResponses(frep.getFiles());
        var category = CategoryMapper.INSTANCE.toResponse(frep.getCategory());
        return FreePatternResponse.builder()
                .id(frep.getId())
                .name(frep.getName())
                .description(frep.getDescription())
                .author(frep.getAuthor())
                .isHome(frep.isHome())
                .link(frep.getLink())
                .content(frep.getContent())
                .status(frep.getStatus())
                .userId(user.getId())
                .username(user.getName())
                .userAvatar(user.getImageUrl())
                .images(images)
                .files(files)
                .category(category)
                .build();
    }

    /**
     * Deletes a FreePattern identified by the given ID.
     *
     * @param id The unique identifier of the FreePattern to delete.
     */
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
        if (!isAdmin && !freePattern.getCreatedBy().equals(currentUser.getId())) {
            throw new AccessDeniedException(MessageConstant.MSG_FORBIDDEN,
                    MAP_CODE.get(MessageConstant.MSG_FORBIDDEN));
        }

        freePatternRepo.delete(freePattern);
    }

    /**
     * Deletes multiple FreePatterns identified by the given IDs.
     *
     * @param ids The list of unique identifiers of the FreePatterns to delete.
     */
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

    /**
     * Prepares a Pageable object based on the given pagination and sorting parameters,
     * and applies filters to identify matching records. The filtered IDs are added
     * to the provided list of freePatternIds.
     *
     * @param pageNo         the page number to retrieve, zero-based index
     * @param pageSize       the number of records per page
     * @param sortBy         the property name to sort by
     * @param sortDir        the direction of sorting, either "asc" for ascending or "desc" for descending
     * @param filters        an array of filters to apply to the query
     * @param freePatternIds a reference to a list where the filtered IDs will be collected
     * @return a Pageable object configured with the specified page, size, and sort properties
     */
    private Pageable preparePageableAndFilter(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters, List<String> freePatternIds) {
        if (filters != null && filters.length > 0) {
            GenericFilter<FreePattern> filter = GenericFilter.create(filters);
            var spec = filter.build();
            List<String> ids = freePatternRepoCustom.findAllIds(spec);
            freePatternIds.addAll(ids);
        }
        Sort.Direction dir = Sort.Direction.fromString(sortDir);
        Sort sort = Sort.by(dir, sortBy);
        return PageRequest.of(pageNo, pageSize, sort);
    }
}
