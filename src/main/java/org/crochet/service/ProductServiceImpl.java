package org.crochet.service;

import org.crochet.exception.ResourceNotFoundException;
import org.crochet.mapper.ProductMapper;
import org.crochet.model.Product;
import org.crochet.repository.ProductRepository;
import org.crochet.repository.ProductSpecifications;
import org.crochet.request.ProductRequest;
import org.crochet.response.ProductPaginationResponse;
import org.crochet.response.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ProductMapper mapper;

    @Transactional
    @Override
    public ProductResponse createOrUpdate(ProductRequest request) {
        var product = productRepo.findById(request.getId()).orElse(null);
        if (product == null) {
            // create product
            product = Product.builder()
                    .id(request.getId())
                    .name(request.getName())
                    .price(request.getPrice())
                    .description(request.getDescription())
                    .build();
        } else {
            // update product
            product.setName(request.getName());
            product.setPrice(request.getPrice());
            product.setDescription(request.getDescription());
        }
        product = productRepo.save(product);
        return mapper.toResponse(product);
    }

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
        List<ProductResponse> contents = mapper.toResponses(menuPage.getContent());

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
    public ProductResponse getDetail(long id) {
        var product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return mapper.toResponse(product);
    }
}
