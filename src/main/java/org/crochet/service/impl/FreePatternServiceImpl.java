package org.crochet.service.impl;

import org.crochet.constant.AppConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FreePatternMapper;
import org.crochet.model.FreePattern;
import org.crochet.payload.request.FreePatternRequest;
import org.crochet.payload.response.FreePatternResponse;
import org.crochet.payload.response.PaginatedFreePatternResponse;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.FreePatternSpecifications;
import org.crochet.service.FreePatternService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * FreePatternServiceImpl class
 */
@Service
public class FreePatternServiceImpl implements FreePatternService {
    private final FreePatternRepository freePatternRepo;

    /**
     * Constructs a new {@code FreePatternServiceImpl} with the specified FreePattern repository.
     *
     * @param freePatternRepo The repository for handling FreePattern-related operations.
     */
    public FreePatternServiceImpl(FreePatternRepository freePatternRepo) {
        this.freePatternRepo = freePatternRepo;
    }

    /**
     * Creates a new FreePattern or updates an existing one based on the provided {@link FreePatternRequest}.
     * If the request contains an ID, it updates the existing FreePattern with the corresponding ID.
     * If the request does not contain an ID, it creates a new FreePattern.
     *
     * @param request The {@link FreePatternRequest} containing information for creating or updating the FreePattern.
     * @return
     */
    @Transactional
    @Override
    public FreePatternResponse createOrUpdate(FreePatternRequest request) {
        FreePattern freePattern = (request.getId() == null) ? new FreePattern()
                : findOne(request.getId());
        freePattern.setName(request.getName());
        freePattern.setDescription(request.getDescription());
        freePattern.setFiles(request.getFiles());
        freePattern = freePatternRepo.save(freePattern);
        return FreePatternMapper.INSTANCE.toResponse(freePattern);
    }

    /**
     * Retrieves a paginated list of FreePatterns based on the provided parameters.
     *
     * @param pageNo   The page number to retrieve (0-indexed).
     * @param pageSize The number of FreePatterns to include in each page.
     * @param sortBy   The attribute by which the FreePatterns should be sorted.
     * @param sortDir  The sorting direction, either "ASC" (ascending) or "DESC" (descending).
     * @param text     The search text used to filter FreePatterns by name or other criteria.
     * @return A {@link PaginatedFreePatternResponse} containing the paginated list of FreePatterns.
     */
    @Override
    public PaginatedFreePatternResponse getFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir,
                                                        String text) {
        // create Sort instance
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<FreePattern> spec = Specification.where(null);
        if (text != null && !text.isEmpty()) {
            spec = spec.and(FreePatternSpecifications.searchBy(text));
        }

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

    @Override
    public List<FreePatternResponse> getLimitedFreePatterns() {
        var freePatterns = freePatternRepo.findAll()
                .stream()
                .limit(AppConstant.FREE_PATTERN_SIZE)
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
        var freePattern = findOne(id);
        return FreePatternMapper.INSTANCE.toResponse(freePattern);
    }

    private FreePattern findOne(String id) {
        return freePatternRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Free pattern not found"));
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        freePatternRepo.deleteById(id);
    }
}
