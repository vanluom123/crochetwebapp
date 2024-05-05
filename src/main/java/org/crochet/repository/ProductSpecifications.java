package org.crochet.repository;

import org.crochet.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecifications {
    public static Specification<Product> searchByNameOrDesc(String searchText) {
        return (root, query, cb) -> {
            String lowerCase = searchText.toLowerCase();
            var nameLike = cb.like(cb.lower(root.get("name")), "%" + lowerCase + "%");
            var descriptionLike = cb.like(cb.lower(root.get("description")), "%" + lowerCase + "%");
            return cb.or(nameLike, descriptionLike);
        };
    }

    public static Specification<Product> in(List<Product> products) {
        return (root, query, cb) -> root.in(products);
    }
}
