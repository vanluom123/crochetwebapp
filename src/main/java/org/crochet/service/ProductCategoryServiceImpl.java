package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.ProductCategoryMapper;
import org.crochet.model.ProductCategory;
import org.crochet.repository.ProductCategoryRepository;
import org.crochet.payload.request.ProductCategoryRequest;
import org.crochet.payload.response.ProductCategoryResponse;
import org.crochet.service.contact.ProductCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * ProductCategoryServiceImpl class
 */
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepo;

    /**
     * Constructs a new {@code ProductCategoryServiceImpl} with the specified product category repository.
     *
     * @param productCategoryRepo The repository for handling product categories.
     */
    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepo) {
        this.productCategoryRepo = productCategoryRepo;
    }

    /**
     * Creates or updates a product category based on the provided {@link ProductCategoryRequest}.
     *
     * @param request The {@link ProductCategoryRequest} containing information for creating or updating the product category.
     * @return A {@link ProductCategoryResponse} representing the created or updated product category.
     * @throws IllegalArgumentException  If the category name is duplicated.
     */
    @Transactional
    @Override
    public ProductCategoryResponse createOrUpdate(ProductCategoryRequest request) {
        if (validateCategoryName(request.getCategoryName())) {
            throw new IllegalArgumentException("Category name is duplicated");
        }
        var category = getOrCreateProductCategory(request);
        category.setCategoryName(request.getCategoryName());
        category = productCategoryRepo.save(category);
        return ProductCategoryMapper.INSTANCE.toResponse(category);
    }

    /**
     * Retrieves an existing product category based on the provided {@link ProductCategoryRequest} ID or creates a new one.
     *
     * @param request The {@link ProductCategoryRequest} containing information for retrieving or creating the product category.
     * @return A {@link ProductCategory} representing the existing or newly created product category.
     * @throws ResourceNotFoundException If an existing product category is to be retrieved, and the specified ID is not found.
     */
    private ProductCategory getOrCreateProductCategory(ProductCategoryRequest request) {
        return (request.getId() == null) ? new ProductCategory()
                : productCategoryRepo.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found"));
    }

    /**
     * Validates whether a product category with the specified name already exists.
     *
     * @param categoryName The name of the product category to be validated.
     * @return {@code true} if a product category with the given name already exists, {@code false} otherwise.
     */
    private boolean validateCategoryName(String categoryName) {
        return productCategoryRepo.findAll()
                .stream()
                .anyMatch(c -> c.getCategoryName().equals(categoryName));
    }

    /**
     * Retrieves all product categories.
     *
     * @return A list of {@link ProductCategoryResponse} representing all product categories.
     */
    @Override
    public List<ProductCategoryResponse> getAll() {
        var categories = productCategoryRepo.findAll();
        return ProductCategoryMapper.INSTANCE.toResponses(categories);
    }
}
