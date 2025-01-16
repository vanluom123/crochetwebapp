package org.crochet.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.constant.MessageConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.ProductMapper;
import org.crochet.model.Product;
import org.crochet.model.Settings;
import org.crochet.payload.request.Filter;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductOnHome;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.repository.CategoryRepo;
import org.crochet.repository.GenericFilter;
import org.crochet.repository.ProductRepository;
import org.crochet.service.ProductService;
import org.crochet.util.ImageUtils;
import org.crochet.util.SettingsUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private final SettingsUtil settingsUtil;

    /**
     * Creates a new product or updates an existing one based on the provided
     * {@link ProductRequest}.
     * <p>
     * If the product with the specified ID exists, its attributes will be updated.
     * If no product with the specified ID is found, a new product will be created.
     *
     * @param request The {@link ProductRequest} containing information for creating
     *                or updating the product.
     * @return A {@link ProductResponse} representing the created or updated
     * product.
     */
    @Transactional
    @Override
    public ProductResponse createOrUpdate(ProductRequest request) {
        Product product;
        var images = ImageUtils.sortFiles(request.getImages());

        if (!StringUtils.hasText(request.getId())) {
            var category = categoryRepo.findById(request.getCategoryId()).orElseThrow(
                    () -> new ResourceNotFoundException(MessageConstant.MSG_CATEGORY_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_CATEGORY_NOT_FOUND)));

            product = Product.builder()
                    .category(category)
                    .name(request.getName())
                    .price(request.getPrice())
                    .description(request.getDescription())
                    .currencyCode(request.getCurrencyCode())
                    .isHome(request.isHome())
                    .link(request.getLink())
                    .content(request.getContent())
                    .images(FileMapper.INSTANCE.toEntities(images))
                    .build();
        } else {
            product = productRepo.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.MSG_PRODUCT_NOT_FOUND,
                            MAP_CODE.get(MessageConstant.MSG_PRODUCT_NOT_FOUND)));

            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setCurrencyCode(request.getCurrencyCode());
            product.setHome(request.isHome());
            product.setContent(request.getContent());
            product.setLink(request.getLink());
            product.setImages(FileMapper.INSTANCE.toEntities(images));
        }
        product = productRepo.save(product);
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .currencyCode(product.getCurrencyCode())
                .isHome(product.isHome())
                .link(product.getLink())
                .content(product.getContent())
                .build();
    }

    /**
     * Retrieves a paginated list of products based on the provided parameters.
     *
     * @param pageNo   The page number to retrieve (0-indexed).
     * @param pageSize The number of products to include in each page.
     * @param sortBy   The attribute by which the products should be sorted.
     * @param sortDir  The sorting direction, either "ASC" (ascending) or "DESC"
     *                 (descending).
     * @param filters  The list of filters.
     * @return A {@link ProductPaginationResponse} containing the paginated list of
     * products.
     */
    @Override
    public ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir,
                                                 Filter[] filters) {
        Specification<Product> spec = Specification.where(null);
        if (filters != null && filters.length > 0) {
            GenericFilter<Product> filter = GenericFilter.create(filters);
            spec = filter.build();
        }

        var prodIds = productRepo.findAll(spec)
                .stream()
                .map(Product::getId)
                .toList();

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        var menuPage = productRepo.findProductOnHomeWithIds(prodIds, pageable);

        return ProductPaginationResponse.builder()
                .contents(menuPage.getContent())
                .pageNo(menuPage.getNumber())
                .pageSize(menuPage.getSize())
                .totalElements(menuPage.getTotalElements())
                .totalPages(menuPage.getTotalPages())
                .last(menuPage.isLast())
                .build();
    }

    /**
     * Get product ids
     *
     * @param pageNo Page number
     * @param limit  Limit
     * @return List of product ids
     */
    @Override
    public List<String> getProductIds(int pageNo, int limit) {
        Pageable pageable = PageRequest.of(pageNo, limit);
        return productRepo.getProductIds(pageable);
    }

    /**
     * Retrieves a list of products that are marked as limited.
     *
     * @return A list of {@link ProductResponse} containing limited products.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Override
    public List<ProductOnHome> getLimitedProducts() {
        Map<String, Settings> settingsMap = settingsUtil.getSettingsMap();
        if (settingsMap.isEmpty()) {
            return Collections.emptyList();
        }

        var direction = settingsMap.get("homepage.product.direction").getValue();

        var orderBy = settingsMap.get("homepage.product.orderBy").getValue();

        var limit = settingsMap.get("homepage.product.limit").getValue();

        Sort sort = Sort.by(Sort.Direction.fromString(direction), orderBy);
        Pageable pageable = PageRequest.of(0, Integer.parseInt(limit), sort);

        return productRepo.findLimitedNumProduct(pageable);
    }

    /**
     * Retrieves detailed information for a specific product identified by the given
     * ID.
     *
     * @param id The unique identifier of the product.
     * @return A {@link ProductResponse} containing detailed information about the
     * product.
     */
    @Override
    public ProductResponse getDetail(String id) {
        var product = productRepo.getDetail(id).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstant.MSG_PRODUCT_NOT_FOUND,
                        MAP_CODE.get(MessageConstant.MSG_PRODUCT_NOT_FOUND)));
        return ProductMapper.INSTANCE.toResponse(product);
    }

    /**
     * Deletes the product with the specified ID.
     *
     * @param id The unique identifier of the product to delete.
     */
    @Transactional
    @Override
    public void delete(String id) {
        productRepo.deleteById(id);
    }

    /**
     * Deletes multiple products with the specified IDs.
     *
     * @param ids The list of unique identifiers of the products to delete.
     */
    @Transactional
    @Override
    public void deleteMultiple(List<String> ids) {
        productRepo.deleteMultiple(ids);
    }
}
