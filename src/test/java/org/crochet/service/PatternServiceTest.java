package org.crochet.service;

import org.crochet.mapper.PatternMapper;
import org.crochet.mapper.PatternMapperImpl;
import org.crochet.model.Pattern;
import org.crochet.repository.PatternRepository;
import org.crochet.response.PatternPaginationResponse;
import org.crochet.service.abstraction.PatternService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatternServiceTest {

  @Mock
  private PatternRepository patternRepository;

  @Mock
  private Page<Pattern> patternPage;

  private PatternService patternService;

  @BeforeEach
  public void setup() {
    PatternMapper patternMapper = new PatternMapperImpl();
    patternService = new PatternServiceImpl(patternRepository, patternMapper);
  }

  @Test
  public void testGetPatterns() {
    // Arrange
    int pageNo = 1;
    int pageSize = 10;
    String sortBy = "name";
    String sortDir = "ASC";
    String text = "test";

    // Mock the behavior of patternRepository
    when(patternRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(patternPage);

    Pattern pattern = Pattern.builder()
        .name("test")
        .image("test")
        .description("test")
        .price(10)
        .build();

    // Mock the behavior of patternPage
    when(patternPage.getContent()).thenReturn(Collections.singletonList(pattern));
    when(patternPage.getNumber()).thenReturn(1);
    when(patternPage.getSize()).thenReturn(10);
    when(patternPage.getTotalElements()).thenReturn(100L);
    when(patternPage.getTotalPages()).thenReturn(10);
    when(patternPage.isLast()).thenReturn(true);

    // Act
    PatternPaginationResponse result = patternService.getPatterns(pageNo, pageSize, sortBy, sortDir, text);

    // Assert
    // Add assertions based on the expected behavior and the mocked objects
    assertEquals(1, result.getPageNo());
    assertEquals(10, result.getPageSize());

    // Verify that certain methods were called
    verify(patternRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
  }
}
