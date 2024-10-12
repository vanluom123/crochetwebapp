package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.AppConstant;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.ProductMapper;
import org.crochet.model.Product;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.GenericFilter;
import org.crochet.repository.ProductRepository;
import org.crochet.repository.SettingsRepo;
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

import static org.crochet.constant.MessageCodeConstant.MAP_CODE;

/**
 * ProductServiceImpl class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepo;
    private final CategoryRepo categoryRepo;
    private final SettingsRepo settingsRepo;

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
                    @CacheEvict(value = "limitedproducts", allEntries = true)
            }
    )
    public ProductResponse createOrUpdate(ProductRequest request) {
        Product product;
        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_CATEGORY_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_CATEGORY_NOT_FOUND))
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
                    () -> new ResourceNotFoundException(MessageConstant.MSG_PRODUCT_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_PRODUCT_NOT_FOUND))
            );
            product = ProductMapper.INSTANCE.update(request, product);
        }
        product = productRepo.save(product);
        return ProductMapper.INSTANCE.toResponse(product);
    }

    /**
     * Retrieves a paginated list of products based on the provided parameters.
     *
     * @param pageNo   The page number to retrieve (0-indexed).
     * @param pageSize The number of products to include in each page.
     * @param sortBy   The attribute by which the products should be sorted.
     * @param sortDir  The sorting direction, either "ASC" (ascending) or "DESC" (descending).
     * @param filters  The list of filters.
     * @return A {@link ProductPaginationResponse} containing the paginated list of products.
     */
    @Override
    public ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, Filter[] filters) {
        Specification<Product> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<Product> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
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
     * Retrieves a list of products that are marked as limited.
     *
     * @return A list of {@link ProductResponse} containing limited products.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Cacheable(value = "limitedproducts")
    @Override
    public List<ProductResponse> getLimitedProducts() {
        log.info("Fetching limited products");
        var direction = settingsRepo.findByKey("homepage.product.direction")
                .orElse(Sort.Direction.ASC.name());
        var orderBy = settingsRepo.findByKey("homepage.product.orderBy")
                .orElse("id");
        var limit = settingsRepo.findByKey("homepage.product.limit")
                .orElse(AppConstant.DEFAULT_LIMIT);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable pageable = PageRequest.of(0, Integer.parseInt(limit), sort);
        var products = productRepo.findLimitedNumProduct(pageable);
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
                () -> new ResourceNotFoundException(MessageConstant.MSG_PRODUCT_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_PRODUCT_NOT_FOUND))
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
                    @CacheEvict(value = "limitedproducts", allEntries = true)
            }
    )
    public void delete(String id) {
        productRepo.deleteById(id);
    }
}
