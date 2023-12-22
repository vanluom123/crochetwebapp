package org.crochet.service;

import org.crochet.constant.AppConstant;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.ProductMapper;
import org.crochet.model.Product;
import org.crochet.repository.ProductCategoryRepository;
import org.crochet.repository.ProductRepository;
import org.crochet.repository.ProductSpecifications;
import org.crochet.request.ProductRequest;
import org.crochet.response.ProductPaginationResponse;
import org.crochet.response.ProductResponse;
import org.crochet.service.contact.ProductService;
import org.crochet.util.ConvertUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * ProductServiceImpl class
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepo;
    private final ProductCategoryRepository productCategoryRepo;

    /**
     * Constructs a new {@code ProductServiceImpl} with the specified product repository.
     *
     * @param productRepo         The repository for handling product-related operations.
     * @param productCategoryRepo The repository for handling product category.
     */
    public ProductServiceImpl(ProductRepository productRepo,
                              ProductCategoryRepository productCategoryRepo) {
        this.productRepo = productRepo;
        this.productCategoryRepo = productCategoryRepo;
    }

    /**
     * Creates a new product or updates an existing one based on the provided {@link ProductRequest}.
     * <p>
     * If the product with the specified ID exists, its attributes will be updated.
     * If no product with the specified ID is found, a new product will be created.
     *
     * @param request The {@link ProductRequest} containing information for creating or updating the product.
     * @param files
     * @return A {@link ProductResponse} representing the created or updated product.
     */
    @Transactional
    @Override
    public ProductResponse createOrUpdate(ProductRequest request, List<MultipartFile> files) {
        var category = productCategoryRepo.findById(UUID.fromString(request.getProductCategoryId()))
                .orElseThrow(() -> new ResourceNotFoundException("Product category not found"));
        var product = (request.getId() == null) ? new Product()
                : findOne(request.getId());
        product.setProductCategory(category);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setFiles(ConvertUtils.convertMultipartToString(files));
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
     * @param text     The search text used to filter products by name or other criteria.
     * @return A {@link ProductPaginationResponse} containing the paginated list of products.
     */
    @Override
    public ProductPaginationResponse getProducts(int pageNo, int pageSize, String sortBy, String sortDir, String text) {
        // create Sort instance
        Sort sort = Sort.by(sortBy);
        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();
        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<Product> spec = Specification.where(null);
        if (text != null && !text.isEmpty()) {
            spec = spec.and(ProductSpecifications.searchBy(text));
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
    public ProductResponse getDetail(String id) {
        var product = findOne(id);
        return ProductMapper.INSTANCE.toResponse(product);
    }

    private Product findOne(String id) {
        return productRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
