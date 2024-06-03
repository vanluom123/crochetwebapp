package org.crochet.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.ProductMapper;
import org.crochet.model.Product;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.repository.CustomCategoryRepo;
import org.crochet.repository.CustomProductRepo;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductServiceImpl class
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepo;
    private final CustomProductRepo customProductRepo;
    private final CustomCategoryRepo customCategoryRepo;

    /**
     * Constructor
     *
     * @param productRepo       The {@link ProductRepository} instance.
     * @param customProductRepo The {@link CustomProductRepo} instance.
     */
    public ProductServiceImpl(ProductRepository productRepo,
                              CustomProductRepo customProductRepo,
                              CustomCategoryRepo customCategoryRepo) {
        this.productRepo = productRepo;
        this.customProductRepo = customProductRepo;
        this.customCategoryRepo = customCategoryRepo;
    }

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
            evict = {@CacheEvict(value = "limitedproducts", allEntries = true)}
    )
    public ProductResponse createOrUpdate(ProductRequest request) {
        Product product;
        if (!StringUtils.hasText(request.getId())) {
            var category = customCategoryRepo.findById(request.getCategoryId());
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
            product = customProductRepo.findById(request.getId());
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
    public ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 String searchText, String categoryId, List<Filter> filters) {
        // create Sort instance
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Specification<Product> spec = Specifications.getSpecificationFromFilters(filters);
        if (StringUtils.hasText(searchText)) {
            spec = spec.or(ProductSpecifications.searchByNameOrDesc(searchText));
        }
        if (StringUtils.hasText(categoryId)) {
            spec = spec.or(ProductSpecifications.in(getProductsByCategory(categoryId)));
        }
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

    private List<Product> getProductsByCategory(String categoryId) {
        var category = customCategoryRepo.findById(categoryId);
        List<Product> products = new ArrayList<>(category.getProducts());
        for (var subCategory : category.getChildren()) {
            products.addAll(getProductsByCategory(subCategory.getId()));
        }
        return products;
    }

    @Cacheable(value = "limitedproducts")
    @Override
    public List<ProductResponse> getLimitedProducts() {
        log.info("Fetching limited products");
        var products = productRepo.findAll()
                .stream()
                .filter(Product::isHome)
                .limit(AppConstant.PRODUCT_LIMITED)
                .toList();
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
        var product = customProductRepo.findById(id);
        return ProductMapper.INSTANCE.toResponse(product);
    }

    @Transactional
    @Override
    @CacheEvict(value = "limitedproducts", allEntries = true)
    public void delete(String id) {
        customProductRepo.deleteById(id);
    }
}
