package org.crochet.service.impl;

import com.turkraft.springfilter.converter.FilterSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.crochet.enums.ResultCode;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.PaginationMapper;
import org.crochet.mapper.ProductMapper;
import org.crochet.model.Product;
import org.crochet.model.Settings;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.PaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.repository.ProductRepository;
import org.crochet.repository.ProductSpecifications;
import org.crochet.service.CategoryService;
import org.crochet.service.ProductService;
import org.crochet.util.ImageUtils;
import org.crochet.util.ObjectUtils;
import org.crochet.util.SettingsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ProductServiceImpl class
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepo;
    private final CategoryService categoryService;
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
     */
    @Transactional
    @Override
    public void createOrUpdate(ProductRequest request) {
        Product product;
        if (!ObjectUtils.hasText(request.getId())) {
            var category = categoryService.findById(request.getCategoryId());
            var images = ImageUtils.sortFiles(request.getImages());
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
            product = findById(request.getId());
            product = ProductMapper.INSTANCE.update(request, product);
        }
        productRepo.save(product);
    }

    /**
     * Retrieves a paginated list of products based on the provided parameters.
     *
     * @param offset     The page number to retrieve (0-indexed).
     * @param limit      The number of products to include in each page.
     * @param sortBy     The attribute by which the products should be sorted.
     * @param sortDir    The sorting direction, either "ASC" (ascending) or "DESC"
     *                (descending).
     * @param categoryId Category id
     * @param spec       Specification
     * @return A {@link org.crochet.payload.response.PaginationResponse} containing the paginated list of
     * products.
     */
    @SuppressWarnings("ConstantValue")
    @Override
    public PaginationResponse<ProductResponse> getProducts(int offset, int limit, String sortBy, String sortDir,
                                                           String categoryId, Specification<Product> spec) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);
        Page<ProductResponse> menuPage;
        var filter = ((FilterSpecification<Product>) spec).getFilter();
        var hasCategory = ObjectUtils.hasText(categoryId);
        if (hasCategory) {
            spec = spec.and(ProductSpecifications.inHierarchy(categoryId));
        }
        if ((filter != null && ObjectUtils.isNotEmpty(filter.getChildren())) || hasCategory) {
            var prodIds = productRepo.findAll(spec)
                    .stream()
                    .map(Product::getId)
                    .toList();
            menuPage = productRepo.findProductWithIds(prodIds, pageable);
        } else {
            menuPage = productRepo.findProductWithPageable(pageable);
        }
        return PaginationMapper.getInstance().toPagination(menuPage);
    }

    /**
     * Get product ids
     *
     * @param offset Page number
     * @param limit  Limit
     * @return List of product ids
     */
    @Override
    public List<String> getProductIds(int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return productRepo.getProductIds(pageable);
    }

    /**
     * Retrieves a list of products that are marked as limited.
     *
     * @return A list of {@link ProductResponse} containing limited products.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Override
    public List<ProductResponse> getLimitedProducts() {
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
    @Transactional(readOnly = true)
    public ProductResponse getDetail(String id) {
        var product = findById(id);
        return ProductMapper.INSTANCE.toResponse(product);
    }

    @Override
    public Product findById(String id) {
        return productRepo.findProductById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        ResultCode.MSG_PRODUCT_NOT_FOUND.message(),
                        ResultCode.MSG_PRODUCT_NOT_FOUND.code()
                ));
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
