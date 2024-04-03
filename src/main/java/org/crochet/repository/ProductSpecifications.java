package org.crochet.repository;

import org.crochet.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecifications {
    public static Specification<Product> searchByNameOrDesc(String name) {
        return (root, query, cb) -> {
            var nameLike = cb.like(root.get("name"), "%" + name + "%");
            var descriptionLike = cb.like(root.get("description"), "%" + name + "%");
            return cb.or(nameLike, descriptionLike);
        };
    }

    public static Specification<Product> in(List<Product> products) {
        return (root, query, cb) -> root.in(products);
    }
}
