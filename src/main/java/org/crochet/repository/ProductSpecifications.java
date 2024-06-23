package org.crochet.repository;

import jakarta.persistence.criteria.JoinType;
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

    public static Specification<Product> getAll() {
        return (r, q, cb) -> {
            if (Long.class != q.getResultType()) {
                r.fetch("images", JoinType.LEFT);
            }
            q.distinct(true);
            return cb.conjunction();
        };
    }
}
