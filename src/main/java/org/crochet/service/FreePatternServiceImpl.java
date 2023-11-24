package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FreePatternMapper;
import org.crochet.model.FreePattern;
import org.crochet.repository.FreePatternRepository;
import org.crochet.repository.FreePatternSpecifications;
import org.crochet.request.FreePatternRequest;
import org.crochet.response.FreePatternResponse;
import org.crochet.response.PaginatedFreePatternResponse;
import org.crochet.service.abstraction.FreePatternService;
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

  private final FreePatternRepository freePatternRepository;
  private final FreePatternMapper freePatternMapper;

  /**
   * Constructor
   *
   * @param freePatternRepository FreePatternRepository
   * @param freePatternMapper FreePatternMapper
   */
  public FreePatternServiceImpl(FreePatternRepository freePatternRepository,
                                FreePatternMapper freePatternMapper) {
    this.freePatternRepository = freePatternRepository;
    this.freePatternMapper = freePatternMapper;
  }

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
      freePattern = freePatternMapper.toFreePattern(request);
    } else {
      freePattern = freePatternRepository.findById(request.getId())
          .orElseThrow(() -> new ResourceNotFoundException("FreePattern with id " + request.getId() + " not found"));

      freePattern.setName(request.getName());
      freePattern.setImage(request.getImage());
      freePattern.setDescription(request.getDescription());
    }
    freePattern = freePatternRepository.save(freePattern);
  }

  /**
   * Get free pattern
   *
   * @param pageNo Page number
   * @param pageSize The size of page
   * @param sortBy Sort by
   * @param sortDir Sort directory
   * @param text Text
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

    Page<FreePattern> page = freePatternRepository.findAll(spec, pageable);
    List<FreePatternResponse> contents = freePatternMapper.toResponses(page.getContent());

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
    var freePattern = freePatternRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Free pattern not found"));
    return freePatternMapper.toResponse(freePattern);
  }
}
