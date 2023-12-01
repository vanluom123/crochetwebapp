package org.crochet.service;

import org.crochet.model.Product;
import org.crochet.model.ProductFile;
import org.crochet.repository.ProductRepository;
import org.crochet.request.ProductRequest;
import org.crochet.response.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest request;
    private ProductFile file;
    private Product existingProduct;

    @BeforeEach
    void setup() {
        // Create new request
        request = new ProductRequest();
        request.setId(UUID.randomUUID().toString());
        request.setName("test");
        request.setDescription("test");
        request.setPrice(100);

        // Create new product file
        file = ProductFile.builder()
                .id(UUID.randomUUID())
                .fileName("test")
                .bytes("test")
                .build();

        // Create new product
        existingProduct = Product.builder()
                .id(UUID.fromString(request.getId()))
                .name("test")
                .description("test")
                .price(100)
                .productFiles(Set.of(file))
                .build();
    }

    @Test
    void testCreateOrUpdate_CreateNewProduct() {
        UUID productId = UUID.fromString(request.getId());

        // Mock the behavior of productRepo
        when(productRepo.findById(productId)).thenReturn(Optional.empty());
        when(productRepo.save(any(Product.class))).thenReturn(existingProduct);

        // Act
        ProductResponse result = productService.createOrUpdate(request);

        // Assert
        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals("test", result.getDescription());
        assertEquals(100, result.getPrice());

        // Add your assertions based on the expected behavior
        verify(productRepo, times(1)).findById(productId);
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateOrUpdate_UpdateExistingProduct() {
        UUID productId = UUID.fromString(request.getId());

        // Update product
        Product updatingProduct = Product.builder()
                .id(productId)
                .name("updated")
                .description("updated")
                .price(200)
                .productFiles(Set.of(file))
                .build();

        // Mock the behavior of productRepo
        when(productRepo.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepo.save(any(Product.class))).thenReturn(updatingProduct);

        // Act
        ProductResponse result = productService.createOrUpdate(request);

        // Assert
        assertNotNull(result);
        assertEquals("updated", result.getName());
        assertEquals("updated", result.getDescription());
        assertEquals(200, result.getPrice());

        // Add your assertions based on the expected behavior
        verify(productRepo, times(1)).findById(productId);
        verify(productRepo, times(1)).save(any(Product.class));
    }
}
