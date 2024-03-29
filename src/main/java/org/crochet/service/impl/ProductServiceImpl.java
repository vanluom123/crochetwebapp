package org.crochet.service.impl;

import org.crochet.constant.AppConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.FileMapper;
import org.crochet.mapper.ProductMapper;
import org.crochet.model.File;
import org.crochet.model.Product;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductPaginationResponse;
import org.crochet.payload.response.ProductResponse;
import org.crochet.properties.MessageCodeProperties;
import org.crochet.repository.ProductRepository;
import org.crochet.repository.ProductSpecifications;
import org.crochet.service.CategoryService;
import org.crochet.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

import static org.crochet.constant.MessageConstant.PRODUCT_NOT_FOUND_MESSAGE;

/**
 * ProductServiceImpl class
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepo;
    private final CategoryService categoryService;
    private final MessageCodeProperties msgCodeProps;

    /**
     * Constructor
     *
     * @param productRepo     The {@link ProductRepository} instance.
     * @param categoryService The {@link CategoryService} instance.
     * @param msgCodeProps    The {@link MessageCodeProperties} instance.
     */
    public ProductServiceImpl(ProductRepository productRepo,
                              CategoryService categoryService,
                              MessageCodeProperties msgCodeProps) {
        this.productRepo = productRepo;
        this.categoryService = categoryService;
        this.msgCodeProps = msgCodeProps;
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
    public ProductResponse createOrUpdate(ProductRequest request) {
        var category = categoryService.findById(request.getCategoryId());
        var product = (request.getId() == null) ? new Product()
                : findOne(request.getId());
        product.setCategory(category);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setCurrencyCode(request.getCurrencyCode());

        if (!ObjectUtils.isEmpty(request.getFiles())) {
            List<File> files = FileMapper.INSTANCE.toEntities(request.getFiles());
            for (var file : files) {
                file.setProduct(product);
            }
            product.setFiles(files);
        }

        product = productRepo.save(product);
        return ProductMapper.INSTANCE.toResponse(product);
    }

    /**
     * Retrieves a paginated list of products based on the provided parameters.
     *
     * @param pageNo      The page number to retrieve (0-indexed).
     * @param pageSize    The number of products to include in each page.
     * @param sortBy      The attribute by which the products should be sorted.
     * @param sortDir     The sorting direction, either "ASC" (ascending) or "DESC" (descending).
     * @param text        The search text used to filter products by name or other criteria.
     * @param categoryIds The unique identifiers of the categories used to filter products.
     * @return A {@link ProductPaginationResponse} containing the paginated list of products.
     */
    @Override
    public ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, String text, List<UUID> categoryIds) {
        // create Sort instance
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Product> spec = Specification.where(null);
        if (text != null && !text.isEmpty()) {
            spec = spec.and(ProductSpecifications.searchBy(text));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            spec = spec.and(ProductSpecifications.filterBy(categoryIds));
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

    @Override
    public List<ProductResponse> getLimitedProducts() {
        var products = productRepo.findAll()
                .stream()
                .limit(AppConstant.PRODUCT_SIZE)
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
    public ProductResponse getDetail(UUID id) {
        var product = findOne(id);
        return ProductMapper.INSTANCE.toResponse(product);
    }

    private Product findOne(UUID id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MESSAGE,
                        msgCodeProps.getCode("PRODUCT_NOT_FOUND_MESSAGE")));
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        productRepo.deleteById(id);
    }
}
