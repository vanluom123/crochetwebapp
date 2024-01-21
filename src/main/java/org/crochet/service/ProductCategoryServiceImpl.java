package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.ProductCategoryMapper;
import org.crochet.model.ProductCategory;
import org.crochet.payload.request.ProductCategoryRequest;
import org.crochet.payload.response.ProductCategoryResponse;
import org.crochet.payload.response.ProductCategoryResponseDto;
import org.crochet.repository.ProductCategoryRepository;
import org.crochet.service.contact.ProductCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
     * @throws IllegalArgumentException If the category name is duplicated.
     */
    @Transactional
    @Override
    public String createOrUpdate(ProductCategoryRequest request) {
        var category = (request.getId() == null) ? new ProductCategory() : findOne(request.getId());
        if (validateCategoryName(request.getCategoryName())) {
            throw new IllegalArgumentException("Category name is duplicated");
        }

        ProductCategory parentCategory = null;
        if (request.getParentCategoryName() != null) {
            if (validateCategoryName(request.getParentCategoryName())) {
                throw new IllegalArgumentException("Category name is duplicated");
            }
            parentCategory = new ProductCategory();
            parentCategory.setCategoryName(request.getParentCategoryName());
            parentCategory = productCategoryRepo.save(parentCategory);
        }

        category.setCategoryName(request.getCategoryName());
        category.setParentCategory(parentCategory);
        productCategoryRepo.save(category);
        return "Create success";
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

    @Override
    public List<ProductCategoryResponseDto> getCategories() {
        return productCategoryRepo.findAll()
                .parallelStream()
                .map(ProductCategoryMapper.INSTANCE::toDto)
                .toList();
    }

    /**
     * Validates whether a product category with the specified name already exists.
     *
     * @param categoryName The name of the product category to be validated.
     * @return {@code true} if a product category with the given name already exists, {@code false} otherwise.
     */
    private boolean validateCategoryName(String categoryName) {
        return productCategoryRepo.findAll()
                .parallelStream()
                .anyMatch(c -> Objects.equals(c.getCategoryName(), categoryName));
    }

    private ProductCategory findOne(String id) {
        return productCategoryRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found"));
    }
}