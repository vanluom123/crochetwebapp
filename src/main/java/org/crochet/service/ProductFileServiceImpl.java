package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.ProductFileMapper;
import org.crochet.model.ProductFile;
import org.crochet.repository.ProductFileRepository;
import org.crochet.repository.ProductRepository;
import org.crochet.response.ProductFileResponse;
import org.crochet.service.contact.ProductFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ProductFileServiceImpl implements ProductFileService {

    @Autowired
    private ProductFileRepository productFileRepo;

    @Autowired
    private ProductRepository productRepo;

    @Transactional
    @Override
    public List<ProductFileResponse> create(MultipartFile[] files, String productId) {
        var product = productRepo.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        final List<ProductFile> productFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            ProductFile productFile = new ProductFile();
            try {
                productFile.setBytes(Base64.getEncoder().encodeToString(file.getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            productFile.setFileName(file.getOriginalFilename());
            productFile.setProduct(product);
            productFiles.add(productFile);
        }
        var results = productFileRepo.saveAll(productFiles);
        return ProductFileMapper.INSTANCE.toResponses(results);
    }
}
