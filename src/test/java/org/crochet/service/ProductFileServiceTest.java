package org.crochet.service;

import org.crochet.model.Product;
import org.crochet.model.ProductFile;
import org.crochet.repository.ProductFileRepository;
import org.crochet.repository.ProductRepository;
import org.crochet.response.ProductFileResponse;
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
public class ProductFileServiceTest {
    @Mock
    private ProductFileRepository productFileRepo;

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ProductFileServiceImpl productFileService;

    @Test
    public void testCreate() {
        // Arrange
        String testByte = "test";
        MultipartFile file = new MockMultipartFile("test", testByte.getBytes());
        MultipartFile[] files = List.of(file).toArray(MultipartFile[]::new);


        ProductFile productFile = ProductFile.builder()
                .fileName("test")
                .bytes(testByte)
                .build();
        Set<ProductFile> mockProductFiles = Set.of(productFile);

        UUID productUUID = UUID.randomUUID();
        Product mockProduct = Product.builder()
                .id(productUUID)
                .name("test")
                .description("test")
                .price(100)
                .productFiles(mockProductFiles)
                .build();

        // Mock the behavior of productRepo
        when(productRepo.findById(productUUID)).thenReturn(Optional.of(mockProduct));

        // Mock the behavior of productFileRepo
        when(productFileRepo.saveAll(any())).thenReturn(mockProductFiles.stream().toList());

        // Act
        List<ProductFileResponse> result = productFileService.create(files, productUUID.toString());

        // Assert
        assertNotNull(result);

        // Add your assertions based on the expected behavior
        verify(productRepo, times(1)).findById(productUUID);
        verify(productFileRepo, times(1)).saveAll(any());
    }
}
