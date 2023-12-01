package org.crochet.service;

import org.crochet.model.Pattern;
import org.crochet.model.PatternFile;
import org.crochet.repository.PatternFileRepository;
import org.crochet.repository.PatternRepository;
import org.crochet.response.PatternFileResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PatternFileServiceTest {
    @Mock
    private PatternFileRepository patternFileRepo;

    @Mock
    private PatternRepository patternRepo;

    @InjectMocks
    private PatternFileServiceImpl patternFileService;

    @Test
    public void testCreate() {
        // Arrange
        String testByte = "test";
        MultipartFile file = new MockMultipartFile("test", testByte.getBytes());
        MultipartFile[] files = List.of(file).toArray(MultipartFile[]::new);

        PatternFile productFile = PatternFile.builder()
                .fileName("test")
                .bytes(testByte)
                .build();
        Set<PatternFile> mockPatternFiles = Set.of(productFile);

        UUID patternId = UUID.randomUUID();
        Pattern mockPattern = Pattern.builder()
                .id(patternId)
                .name("test")
                .description("test")
                .price(100)
                .patternFiles(mockPatternFiles)
                .build();

        // Mock the behavior of patternRepo
        when(patternRepo.findById(patternId)).thenReturn(Optional.of(mockPattern));

        // Mock the behavior of patternFileRepo
        when(patternFileRepo.saveAll(any())).thenReturn(mockPatternFiles.stream().toList());

        // Act
        List<PatternFileResponse> result = patternFileService.create(files, patternId.toString());

        // Assert
        assertNotNull(result);

        // Add your assertions based on the expected behavior
        verify(patternRepo, times(1)).findById(patternId);
        verify(patternFileRepo, times(1)).saveAll(any());
    }
}
