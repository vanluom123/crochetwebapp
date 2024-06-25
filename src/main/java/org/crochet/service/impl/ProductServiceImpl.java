package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.ProductMapper;
import org.crochet.model.Product;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.Filter;
import org.crochet.repository.ProductRepository;
import org.crochet.repository.ProductSpecifications;
import org.crochet.repository.Specifications;
import org.crochet.service.ProductService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * ProductServiceImpl class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    final ProductRepository productRepo;
    final CategoryRepo categoryRepo;

    /**
     * Creates a new product or updates an existing one based on the provided {@link ProductRequest}.
     * <p>
     * If the product with the specified ID exists, its attributes will be updated.
     * If no product with the specified ID is found, a new product will be created.
     *
     * @param request The {@link ProductRequest} containing information for creating or updating the product.
     * @return A {@link ProductResponse} representing the created or updated product.
     */
    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "limitedproducts", allEntries = true),
                    @CacheEvict(value = "products", allEntries = true)
            }
    )
    public ProductResponse createOrUpdate(ProductRequest request) {
        Product product;
        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                    () -> new IllegalArgumentException("Category not found")
            );
            product = Product.builder()
                    .category(category)
                    .name(request.getName())
                    .price(request.getPrice())
                    .description(request.getDescription())
                    .currencyCode(request.getCurrencyCode())
                    .isHome(request.isHome())
                    .link(request.getLink())
                    .content(request.getContent())
                    .images(FileMapper.INSTANCE.toEntities(request.getImages()))
                    .build();
        } else {
            product = productRepo.findById(request.getId()).orElseThrow(
                    () -> new IllegalArgumentException("Product not found")
            );
            product = ProductMapper.INSTANCE.update(request, product);
        }
        product = productRepo.save(product);
        return ProductMapper.INSTANCE.toResponse(product);
    }

    /**
     * Retrieves a paginated list of products based on the provided parameters.
     *
     * @param pageNo     The page number to retrieve (0-indexed).
     * @param pageSize   The number of products to include in each page.
     * @param sortBy     The attribute by which the products should be sorted.
     * @param sortDir    The sorting direction, either "ASC" (ascending) or "DESC" (descending).
     * @param searchText The text used to filter products by name or description.
     * @param categoryId The unique identifiers of the categories used to filter products.
     * @param filters    The list of filters
     * @return A {@link ProductPaginationResponse} containing the paginated list of products.
     */
    @Override
    @Cacheable(value = "products",
            key = "T(java.util.Objects).hash(#pageNo, #pageSize, #sortBy, #sortDir, #searchText, #categoryId, #filters)")
    public ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 String searchText, String categoryId, List<Filter> filters) {
        log.info("Fetching products");
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Product> spec = Specifications.getSpecFromFilters(filters);

        if (StringUtils.hasText(searchText)) {
            spec = spec.or(ProductSpecifications.searchByNameOrDesc(searchText));
        }
        if (StringUtils.hasText(categoryId)) {
            spec = spec.or(ProductSpecifications.in(getProductsByCategory(categoryId)));
        }

        spec = spec.and(ProductSpecifications.getAll());

        Page<Product> menuPage = productRepo.findAll(spec, pageable);

        List<ProductResponse> contents = ProductMapper.INSTANCE.toResponses(menuPage.getContent());
        return ProductPaginationResponse.builder()
                .contents(contents)
                .pageNo(menuPage.getNumber())
                .pageSize(menuPage.getSize())
                .totalElements(menuPage.getTotalElements())
                .totalPages(menuPage.getTotalPages())
                .last(menuPage.isLast())
                .build();
    }

    /**
     * Get all products by category
     *
     * @param categoryId The unique identifier of the category.
     * @return A list of {@link Product} containing products.
     */
    private List<Product> getProductsByCategory(String categoryId) {
        List<Product> products = productRepo.findProductByCategory(categoryId);
        var categoryIds = categoryRepo.findChildrenIds(categoryId);
        for (var subCategoryId : categoryIds) {
            products.addAll(getProductsByCategory(subCategoryId));
        }
        return products;
    }

    /**
     * Retrieves a list of products that are marked as limited.
     *
     * @return A list of {@link ProductResponse} containing limited products.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Cacheable(value = "limitedproducts")
    @Override
    public List<ProductResponse> getLimitedProducts() {
        log.info("Fetching limited products");
        var products = productRepo.findLimitedNumProductByCreatedDateDesc(AppConstant.PRODUCT_LIMITED);
        return ProductMapper.INSTANCE.toResponses(products);
    }

    /**
     * Retrieves detailed information for a specific product identified by the given ID.
     *
     * @param id The unique identifier of the product.
     * @return A {@link ProductResponse} containing detailed information about the product.
     */
    @Override
    public ProductResponse getDetail(String id) {
        var product = productRepo.getDetail(id).orElseThrow(
                () -> new IllegalArgumentException("Product not found")
        );
        return ProductMapper.INSTANCE.toResponse(product);
    }

    /**
     * Deletes the product with the specified ID.
     *
     * @param id The unique identifier of the product to delete.
     */
    @Transactional
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "limitedproducts", allEntries = true),
                    @CacheEvict(value = "products", allEntries = true)
            }
    )
    public void delete(String id) {
        productRepo.deleteById(id);
    }
}
