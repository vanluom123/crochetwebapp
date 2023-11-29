package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Pattern;
import org.crochet.repository.PatternRepository;
import org.crochet.request.PatternRequest;
import org.crochet.response.PatternPaginationResponse;
import org.crochet.response.PatternResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatternServiceTest {

  @Mock
  private PatternRepository patternRepository;

  @Mock
  private Page<Pattern> patternPage;

  @InjectMocks
  private PatternServiceImpl patternService;

  @Test
  public void testCreateOrUpdate() {
    // Create request
    PatternRequest request = new PatternRequest();
    request.setId(1);
    request.setName("test");
    request.setDescription("test");
    request.setPrice(10);

    // Create pattern
    Pattern existingPattern = Pattern.builder()
        .id(1L)
        .name("test")
        .description("test")
        .price(10)
        .build();

    // Mock the behavior of patternRepository
    when(patternRepository.findById(anyLong())).thenReturn(Optional.ofNullable(existingPattern));
    when(patternRepository.save(any())).thenReturn(existingPattern);

    // Act
    patternService.createOrUpdate(request);

    // Assert
    verify(patternRepository, times(1)).findById(anyLong());
    verify(patternRepository, times(1)).save(any());
  }

  @SuppressWarnings("unchecked")
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
    assertEquals(1, result.getPageNo());
    assertEquals(10, result.getPageSize());

    // Verify that certain methods were called
    verify(patternRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
  }

  @Test
  public void testGetDetail() {
    // Arrange
    long patternId = 1L;

    Pattern existingPattern = Pattern.builder()
        .id(patternId)
        .name("test")
        .description("test")
        .price(10)
        .build();

    // Mock the behavior of patternRepository
    when(patternRepository.findById(anyLong())).thenReturn(Optional.ofNullable(existingPattern));

    // Act
    PatternResponse result = patternService.getDetail(patternId);

    // Assert
    assertNotNull(result);

    // Verify that certain methods were called
    verify(patternRepository, times(1)).findById(anyLong());
  }

  @Test
  public void testGetDetailNotFound() {
    // Arrange
    long nonExistentPatternId = 2L;

    // Mock the behavior of patternRepository when the pattern is not found
    when(patternRepository.findById(anyLong())).thenReturn(Optional.empty());

    // Assert
    // Expecting ResourceNotFoundException to be thrown
    var thrown = assertThrows(ResourceNotFoundException.class, () -> patternService.getDetail(nonExistentPatternId), "Pattern not found");
    assertEquals("Pattern not found", thrown.getMessage());
  }
}
