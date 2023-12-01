package org.crochet.service;

import org.crochet.model.FreePattern;
import org.crochet.model.FreePatternFile;
import org.crochet.repository.FreePatternFileRepository;
import org.crochet.repository.FreePatternRepository;
import org.crochet.response.FreePatternFileResponse;
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
public class FreePatternFileServiceTest {
    @Mock
    private FreePatternFileRepository freePatternFileRepo;

    @Mock
    private FreePatternRepository freePatternRepo;

    @InjectMocks
    private FreePatternFileServiceImpl patternFileService;

    @Test
    public void testCreate() {
        // Arrange
        String testByte = "test";
        MultipartFile file = new MockMultipartFile("test", testByte.getBytes());
        MultipartFile[] files = List.of(file).toArray(MultipartFile[]::new);

        FreePatternFile freePatternFile = FreePatternFile.builder()
                .fileName("test")
                .bytes(testByte)
                .build();
        Set<FreePatternFile> mockFreePatternFiles = Set.of(freePatternFile);

        UUID freePatternId = UUID.randomUUID();
        FreePattern mockFreePattern = FreePattern.builder()
                .id(freePatternId)
                .name("test")
                .description("test")
                .freePatternFiles(mockFreePatternFiles)
                .build();

        // Mock the behavior of patternRepo
        when(freePatternRepo.findById(freePatternId)).thenReturn(Optional.of(mockFreePattern));

        // Mock the behavior of patternFileRepo
        when(freePatternFileRepo.saveAll(any())).thenReturn(mockFreePatternFiles.stream().toList());

        // Act
        List<FreePatternFileResponse> result = patternFileService.create(files, freePatternId.toString());

        // Assert
        assertNotNull(result);

        // Add your assertions based on the expected behavior
        verify(freePatternRepo, times(1)).findById(freePatternId);
        verify(freePatternFileRepo, times(1)).saveAll(any());
    }
}
