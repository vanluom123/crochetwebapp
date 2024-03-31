package org.crochet.repository;

import org.crochet.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecifications {
    public static Specification<Product> in(List<Product> products) {
        return (root, query, cb) -> root.in(products);
    }
}
