package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FreePatternMapper;
import org.crochet.model.FreePattern;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.FreePatternSpecifications;
import org.crochet.request.FreePatternRequest;
import org.crochet.response.FreePatternResponse;
import org.crochet.response.PaginatedFreePatternResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FreePatternServiceImpl implements FreePatternService {

    @Autowired
    private FreePatternRepository freePatternRepo;

    @Autowired
    private FreePatternMapper mapper;

    /**
     * Create or update free pattern
     *
     * @param request FreePatternRequest
     */
    @Transactional
    @Override
    public void createOrUpdate(FreePatternRequest request) {
        FreePattern freePattern;

        if (request.getId() == null) {
            freePattern = mapper.toFreePattern(request);
        } else {
            freePattern = freePatternRepo.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("FreePattern with id " + request.getId() + " not found"));

            freePattern.setName(request.getName());
            freePattern.setDescription(request.getDescription());
        }
        freePatternRepo.save(freePattern);
    }

    /**
     * Get free pattern
     *
     * @param pageNo   Page number
     * @param pageSize The size of page
     * @param sortBy   Sort by
     * @param sortDir  Sort directory
     * @param text     Text
     * @return Free pattern is paginated
     */
    @Override
    public PaginatedFreePatternResponse getFreePatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text) {
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
        List<FreePatternResponse> contents = mapper.toResponses(page.getContent());

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
     * Get detail
     *
     * @param id Id
     * @return Response
     */
    @Override
    public FreePatternResponse getDetail(long id) {
        var freePattern = freePatternRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Free pattern not found"));
        return mapper.toResponse(freePattern);
    }
}
