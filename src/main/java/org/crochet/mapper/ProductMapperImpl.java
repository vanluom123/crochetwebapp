package org.crochet.mapper;

import org.crochet.model.Product;
import org.crochet.model.ProductImage;
import org.crochet.response.ProductResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class ProductMapperImpl implements ProductMapper {
    @Override
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        var images = product.getProductImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();

        return ProductResponse.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .images(images)
                .build();
    }

    @Override
    public List<ProductResponse> toResponses(List<Product> products) {
        if (ObjectUtils.isEmpty(products)) {
            return null;
        }

        return products.stream()
                .map(this::toResponse)
                .toList();
    }
}
