package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.Pattern;
import org.crochet.model.PatternFile;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @InjectMocks
    private PatternServiceImpl patternService;

    @Test
    void testCreateOrUpdate() {
        // Create request
        PatternRequest request = new PatternRequest();
        request.setId(UUID.randomUUID().toString());
        request.setName("test");
        request.setDescription("test");
        request.setPrice(10);

        // Create pattern
        Pattern existingPattern = Pattern.builder()
                .id(UUID.randomUUID())
                .name("test")
                .description("test")
                .price(10)
                .build();

        // Mock the behavior of patternRepository
        when(patternRepository.findById(any())).thenReturn(Optional.ofNullable(existingPattern));
        when(patternRepository.save(any())).thenReturn(existingPattern);

        // Act
        patternService.createOrUpdate(request);

        // Assert
        verify(patternRepository, times(1)).findById(any());
        verify(patternRepository, times(1)).save(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetPatterns() {
        int pageNo = 1;
        int pageSize = 10;
        String sortBy = "name";
        String sortDir = "ASC";
        String text = "test";

        // Init pattern file
        PatternFile mockPatternFile = PatternFile.builder()
                .id(UUID.randomUUID())
                .fileName("test")
                .bytes("test")
                .build();
        // Init pattern
        Pattern mockPattern = Pattern.builder()
                .id(UUID.randomUUID())
                .name("test")
                .description("test")
                .price(10)
                .patternFiles(Set.of(mockPatternFile))
                .build();

        // Mock the behavior of patternRepository
        when(patternRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(patternPage);

        // Mock the behavior of patternPage
        when(patternPage.getContent()).thenReturn(Collections.singletonList(mockPattern));
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
    void testGetDetail() {
        // Arrange
        UUID patternId = UUID.randomUUID();
        PatternFile mockPatternFile = PatternFile.builder()
                .id(UUID.randomUUID())
                .fileName("test")
                .bytes("test")
                .build();
        Pattern mockPattern = Pattern.builder()
                .id(patternId)
                .name("test")
                .description("test")
                .price(10)
                .patternFiles(Set.of(mockPatternFile))
                .build();

        // Mock the behavior of patternRepository
        when(patternRepository.findById(any())).thenReturn(Optional.ofNullable(mockPattern));

        // Act
        PatternResponse result = patternService.getDetail(patternId.toString());

        // Assert
        assertNotNull(result);

        // Verify that certain methods were called
        verify(patternRepository, times(1)).findById(any());
    }

    @Test
    void testGetDetailNotFound() {
        // Arrange
        UUID nonExistentPatternId = UUID.randomUUID();

        // Mock the behavior of patternRepository when the pattern is not found
        when(patternRepository.findById(any())).thenReturn(Optional.empty());

        // Assert
        // Expecting ResourceNotFoundException to be thrown
        var thrown = assertThrows(ResourceNotFoundException.class, () -> patternService.getDetail(nonExistentPatternId.toString()), "Pattern not found");
        assertEquals("Pattern not found", thrown.getMessage());
    }
}
