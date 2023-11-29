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

import java.util.ArrayList;
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
    public List<ProductFileResponse> create(List<String> fileNames, String productId) {
        var product = productRepo.findById(UUID.fromString(productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        final List<ProductFile> productFiles = new ArrayList<>();
        fileNames.forEach(fileName -> {
            ProductFile productFile = new ProductFile();
            productFile.setFileUrl(fileName);
            productFile.setProduct(product);
            productFiles.add(productFile);
        });
        var results = productFileRepo.saveAll(productFiles);
        return ProductFileMapper.INSTANCE.toResponses(results);
    }
}
