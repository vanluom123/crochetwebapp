package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.PatternMapper;
import org.crochet.model.Pattern;
import org.crochet.repository.PatternRepository;
import org.crochet.repository.PatternSpecifications;
import org.crochet.request.PatternRequest;
import org.crochet.response.PatternPaginationResponse;
import org.crochet.response.PatternResponse;
import org.crochet.service.abstraction.PatternService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatternServiceImpl implements PatternService {

  private final PatternRepository patternRepository;
  private final PatternMapper patternMapper;

  public PatternServiceImpl(PatternRepository patternRepository, PatternMapper patternMapper) {
    this.patternRepository = patternRepository;
    this.patternMapper = patternMapper;
  }

  @Transactional
  @Override
  public void createOrUpdate(PatternRequest request) {
    var pattern = patternRepository.findById(request.getId()).orElse(null);
    if (pattern == null) {
      pattern = patternMapper.toPattern(request);
    } else {
      pattern = Pattern.builder()
          .name(request.getName())
          .price(request.getPrice())
          .image(request.getImage())
          .description(request.getDescription())
          .build();
    }

    pattern = patternRepository.save(pattern);
  }

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

    Page<Pattern> menuPage = patternRepository.findAll(spec, pageable);
    List<PatternResponse> responses = patternMapper.toResponses(menuPage.getContent());

    return PatternPaginationResponse.builder()
        .responses(responses)
        .pageNo(menuPage.getNumber())
        .pageSize(menuPage.getSize())
        .totalElements(menuPage.getTotalElements())
        .totalPages(menuPage.getTotalPages())
        .last(menuPage.isLast())
        .build();
  }

  @Override
  public PatternResponse getDetail(long id) {
    var pattern = patternRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pattern not found"));
    return patternMapper.toResponse(pattern);
  }
}
