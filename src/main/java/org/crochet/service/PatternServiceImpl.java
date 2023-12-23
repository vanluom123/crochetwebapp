package org.crochet.service;

import org.crochet.constant.AppConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Pattern;
import org.crochet.model.User;
import org.crochet.payload.request.PatternRequest;
import org.crochet.payload.response.PatternPaginationResponse;
import org.crochet.payload.response.PatternResponse;
import org.crochet.repository.OrderPatternDetailRepository;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.PatternSpecifications;
import org.crochet.repository.UserRepository;
import org.crochet.security.UserPrincipal;
import org.crochet.service.contact.PatternService;
import org.crochet.util.ConvertUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * PatternServiceImpl class
 */
@Service
public class PatternServiceImpl implements PatternService {
    private final PatternRepository patternRepo;
    private final UserRepository userRepo;

    public PatternServiceImpl(PatternRepository patternRepo,
                              UserRepository userRepo) {
        this.patternRepo = patternRepo;
        this.userRepo = userRepo;
    }

    /**
     * Create or update pattern
     *
     * @param request PatternRequest
     * @param files
     */
    @Transactional
    @Override
    public String createOrUpdate(PatternRequest request, List<MultipartFile> files) {
        var pattern = (request.getId() == null) ? new Pattern()
                : findOne(request.getId());
        pattern.setName(request.getName());
        pattern.setPrice(request.getPrice());
        pattern.setDescription(request.getDescription());
        pattern.setCurrencyCode(request.getCurrencyCode());
        pattern.setFiles(ConvertUtils.convertMultipartToString(files));
        patternRepo.save(pattern);
        return "Create pattern successfully";
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

    @Override
    public List<PatternResponse> getLimitedPatterns() {
        var patterns = patternRepo.findAll()
                .stream()
                .limit(AppConstant.PATTERN_SIZE)
                .toList();
        return PatternMapper.INSTANCE.toResponses(patterns);
    }

    /**
     * Get pattern detail
     *
     * @param id Id
     * @return Pattern response
     */
    @Override
    public PatternResponse getDetail(String id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ResourceNotFoundException("User hasn't signed in");
        }
        User user = userRepo.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not existed in database"));
        var pattern = findPatternByUserOrdered(user.getId(), id);
        return PatternMapper.INSTANCE.toResponse(pattern);
    }

    private Pattern findOne(String id) {
        return patternRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Pattern not found"));
    }

    private Pattern findPatternByUserOrdered(UUID userId, String patternId) {
        return patternRepo.findPatternByUserOrdered(userId,
                UUID.fromString(patternId))
                .orElseThrow(() -> new ResourceNotFoundException("User not payment for this pattern"));
    }
}
