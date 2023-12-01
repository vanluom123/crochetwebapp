package org.crochet.service;

import org.crochet.mapper.ProductCategoryMapper;
import org.crochet.model.ProductCategory;
import org.crochet.repository.ProductCategoryRepository;
import org.crochet.request.ProductCategoryRequest;
import org.crochet.response.ProductCategoryResponse;
import org.crochet.service.contact.ProductCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepo;
    private final ProductCategoryMapper mapper;

    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepo,
                                      ProductCategoryMapper mapper) {
        this.productCategoryRepo = productCategoryRepo;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public ProductCategoryResponse createOrUpdate(ProductCategoryRequest request) {
        var category = productCategoryRepo.findById(UUID.fromString(request.getId()))
                .orElse(null);
        if (category == null) {
            category = new ProductCategory();
        }
        category.setCategoryName(request.getCategoryName());
        category = productCategoryRepo.save(category);
        return mapper.toResponse(category);
    }

    @Override
    public List<ProductCategoryResponse> getAll() {
        var categories = productCategoryRepo.findAll();
        return mapper.toResponses(categories);
    }
}
