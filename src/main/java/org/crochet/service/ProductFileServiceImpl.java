package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.ProductFileMapper;
import org.crochet.model.ProductFile;
import org.crochet.repository.ProductFileRepository;
import org.crochet.repository.ProductRepository;
import org.crochet.response.ProductFileResponse;
import org.crochet.service.contact.ProductFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * ProductFileServiceImpl class
 */
@Service
public class ProductFileServiceImpl implements ProductFileService {
    private final ProductFileRepository productFileRepo;

    private final ProductRepository productRepo;

    /**
     * Constructs a new {@code ProductFileServiceImpl} with the specified repositories.
     *
     * @param productFileRepo The repository for handling product files.
     * @param productRepo     The repository for handling products.
     */
    public ProductFileServiceImpl(ProductFileRepository productFileRepo,
                                  ProductRepository productRepo) {
        this.productFileRepo = productFileRepo;
        this.productRepo = productRepo;
    }

    /**
     * Creates product files associated with a specified product.
     *
     * @param files     An array of {@link MultipartFile} objects representing the uploaded files.
     * @param productId The identifier of the product to which the files will be associated.
     * @return A list of {@link ProductFileResponse} objects representing the created product files.
     * @throws ResourceNotFoundException If the specified productId does not correspond to an existing product.
     * @throws RuntimeException          If there is an error while processing the files or saving them to the repository.
     */
    @Transactional
    @Override
    public List<ProductFileResponse> create(MultipartFile[] files, String productId) {
        var product = productRepo.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        final List<ProductFile> productFiles = Stream.of(files)
                .map(file -> {
                    var builder = ProductFile.builder();
                    try {
                        builder.bytes(Base64.getEncoder().encodeToString(file.getBytes()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    builder.fileName(file.getOriginalFilename());
                    builder.product(product);
                    return builder.build();
                }).toList();
        var results = productFileRepo.saveAll(productFiles);
        return ProductFileMapper.INSTANCE.toResponses(results);
    }
}
