package org.crochet.mapper;

import org.crochet.model.Product;
import org.crochet.payload.request.ProductRequest;
import org.crochet.payload.response.ProductResponse;
import org.crochet.util.ImageUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {FileMapper.class, CategoryMapper.class}
)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "isHome", source = "home")
    @Mapping(target = "currencyCode", source = "currencyCode")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponses(Collection<Product> products);

    default Product update(ProductRequest request, Product product) {
        if (product == null) {
            return null;
        }
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != product.getPrice()) {
            product.setPrice(request.getPrice());
        }
        if (request.getCurrencyCode() != null) {
            product.setCurrencyCode(request.getCurrencyCode());
        }
        if (request.isHome() != product.isHome()) {
            product.setHome(request.isHome());
        }
        if (request.getLink() != null) {
            product.setLink(request.getLink());
        }
        if (request.getContent() != null) {
            product.setContent(request.getContent());
        }
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            var sortedImages = ImageUtils.sortFiles(request.getImages());
            product.setImages(FileMapper.INSTANCE.toEntities(sortedImages));
        }
        return product;
    }
}