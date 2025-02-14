package org.crochet.service.impl;

import com.turkraft.springfilter.converter.FilterSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.enums.ResultCode;
import org.crochet.enums.RoleType;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.CategoryMapper;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.FreePatternMapper;
import org.crochet.mapper.PaginationMapper;
import org.crochet.model.FreePattern;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.repository.FreePatternRepoCustom;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.FreePatternSpecifications;
import org.crochet.service.CategoryService;
import org.crochet.service.FreePatternService;
import org.crochet.service.PermissionService;
import org.crochet.service.UserService;
import org.crochet.util.ImageUtils;
import org.crochet.util.ObjectUtils;
import org.crochet.util.SecurityUtils;
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

/**
 * FreePatternServiceImpl class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FreePatternServiceImpl implements FreePatternService {
    private final FreePatternRepository freePatternRepo;
    private final SettingsUtil settingsUtil;
    private final FreePatternRepoCustom freePatternRepoCustom;
    private final PermissionService permissionService;
    private final CategoryService categoryService;
    private final UserService userService;

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
        if (!ObjectUtils.hasText(request.getId())) {
            var category = categoryService.findById(request.getCategoryId());
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
                    .files(FileMapper.INSTANCE.toEntities(sortedFiles))
                    .images(FileMapper.INSTANCE.toEntities(sortedImages))
                    .build();
        } else {
            freePattern = findById(request.getId());
            permissionService.checkUserPermission(freePattern, "update");
            freePattern = FreePatternMapper.INSTANCE.update(request, freePattern);
        }
        freePatternRepo.save(freePattern);
    }

    /**
     * Get all free patterns with filter
     *
     * @param offset  Page number
     * @param limit   Page size
     * @param sortBy  Sort by
     * @param sortDir Sort direction
     * @param categoryId Category id
     * @param spec    Specification<FreePattern>
     * @return PaginatedFreePatternResponse
     */
    @SuppressWarnings("ConstantValue")
    @Transactional(readOnly = true)
    @Override
    public PaginationResponse<FreePatternResponse> getAllFreePatterns(int offset, int limit, String sortBy, String sortDir, String categoryId, Specification<FreePattern> spec) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);
        var filter = ((FilterSpecification<FreePattern>) spec).getFilter();
        var hasCategory = categoryId != null && !categoryId.isBlank();
        Page<FreePatternResponse> page;
        if (hasCategory) {
            spec = spec.and(FreePatternSpecifications.getAllByCategoryId(categoryId));
        }
        if ((filter != null && ObjectUtils.isNotEmpty(filter.getChildren())) || hasCategory) {
            List<String> ids = freePatternRepoCustom.findAllIds(spec);
            page = freePatternRepo.getFrepByIds(ids, pageable);
        } else {
            page = freePatternRepo.getFrepWithPageable(pageable);
        }
        return PaginationMapper.getInstance().toPagination(page);
    }

    /**
     * Retrieves a paginated and sorted list of free patterns associated with a specific user,
     * optionally filtered by specified criteria.
     *
     * @param offset  the page number to retrieve (zero-based)
     * @param limit   the number of items per page
     * @param sortBy  the attribute to sort the results by
     * @param sortDir the direction to sort the results (e.g., "asc" or "desc")
     * @param userId  the ID of the user whose free patterns are to be retrieved
     * @param spec    the specification for filtering the results
     * @return a {@link PaginationResponse} containing the paginated and filtered list of free patterns
     */
    @SuppressWarnings("ConstantValue")
    @Transactional(readOnly = true)
    @Override
    public PaginationResponse<FreePatternResponse> getAllByUser(int offset,
                                                                int limit,
                                                                String sortBy,
                                                                String sortDir,
                                                                String userId,
                                                                Specification<FreePattern> spec) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);
        var filter = ((FilterSpecification<FreePattern>) spec).getFilter();
        Page<FreePatternResponse> page;
        if (filter != null && ObjectUtils.isNotEmpty(filter.getChildren())) {
            var freePatternIds = freePatternRepoCustom.findAllIds(spec);
            page = freePatternRepo.getByUserAndIds(userId, freePatternIds, pageable);
        } else {
            page = freePatternRepo.getByUserWithPageable(userId, pageable);
        }
        return PaginationMapper.getInstance().toPagination(page);
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
     * @param offset Page number
     * @param limit  Limit
     * @return List of free pattern ids
     */
    @Override
    public List<String> getFreePatternIds(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.message(),
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.code()
                ));
        var user = userService.getById(frep.getCreatedBy());
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
        var freePattern = freePatternRepo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.message(),
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.code()
                ));
        permissionService.checkUserPermission(freePattern, "delete");
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
            throw new ResourceNotFoundException(
                    ResultCode.MSG_USER_LOGIN_REQUIRED.message(),
                    ResultCode.MSG_USER_LOGIN_REQUIRED.code()
            );
        }

        // Delete all free patterns if user is admin. Otherwise, delete only free patterns created by the user
        if (currentUser.getRole() == RoleType.ADMIN) {
            freePatternRepo.deleteAllById(ids);
        } else {
            freePatternRepo.deleteAllByIdAndCreatedBy(ids, currentUser.getId());
        }
    }

    /**
     * Get free patterns by collection id
     *
     * @param collectionId Collection id
     * @param offset       Page number
     * @param limit        Page size
     * @param sortBy       Sort by
     * @param sortDir      Sort direction
     * @return PaginationResponse
     */
    @Override
    public PaginationResponse<FreePatternResponse>
    getFrepsByCollectionId(String collectionId,
                           int offset,
                           int limit,
                           String sortBy,
                           String sortDir) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.Direction.fromString(sortDir), sortBy);
        var frepResponse = freePatternRepo.getFrepsByCollection(collectionId, pageable);
        return PaginationMapper.getInstance().toPagination(frepResponse);
    }

    @Override
    public FreePattern findById(String id) {
        return freePatternRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.message(),
                        ResultCode.MSG_FREE_PATTERN_NOT_FOUND.code()
                ));
    }
}
