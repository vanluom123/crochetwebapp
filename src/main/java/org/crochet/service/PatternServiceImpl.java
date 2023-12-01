package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Pattern;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.PatternSpecifications;
import org.crochet.request.PatternRequest;
import org.crochet.response.PatternPaginationResponse;
import org.crochet.response.PatternResponse;
import org.crochet.service.contact.PatternService;
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
 * PatternServiceImpl class
 */
@Service
public class PatternServiceImpl implements PatternService {

    private final PatternRepository patternRepo;

    public PatternServiceImpl(PatternRepository patternRepo) {
        this.patternRepo = patternRepo;
    }

    /**
     * Create or update pattern
     *
     * @param request PatternRequest
     */
    @Transactional
    @Override
    public void createOrUpdate(PatternRequest request) {
        var pattern = patternRepo.findById(UUID.fromString(request.getId()))
                .orElse(null);
        if (pattern == null) {
            pattern = new Pattern();
        }
        pattern.setName(request.getName());
        pattern.setPrice(request.getPrice());
        pattern.setDescription(request.getDescription());
        patternRepo.save(pattern);
    }

    /**
     * Get patterns
     *
     * @param pageNo   Page number
     * @param pageSize The size of page
     * @param sortBy   Sort by
     * @param sortDir  Sort directory
     * @param text     Text
     * @return Pattern is paginated
     */
    @Override
    public PatternPaginationResponse getPatterns(int pageNo, int pageSize, String sortBy, String sortDir, String text) {
        // create Sort instance
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Pattern> spec = Specification.where(null);
        if (text != null && !text.isEmpty()) {
            spec = spec.and(PatternSpecifications.searchBy(text));
        }

        Page<Pattern> menuPage = patternRepo.findAll(spec, pageable);
        List<PatternResponse> responses = PatternMapper.INSTANCE.toResponses(menuPage.getContent());

        return PatternPaginationResponse.builder()
                .contents(responses)
                .pageNo(menuPage.getNumber())
                .pageSize(menuPage.getSize())
                .totalElements(menuPage.getTotalElements())
                .totalPages(menuPage.getTotalPages())
                .last(menuPage.isLast())
                .build();
    }

    /**
     * Get pattern detail
     *
     * @param id Id
     * @return Pattern response
     */
    @Override
    public PatternResponse getDetail(String id) {
        var pattern = patternRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Pattern not found"));
        return PatternMapper.INSTANCE.toResponse(pattern);
    }
}
