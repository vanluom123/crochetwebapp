package org.crochet.repository;

import jakarta.persistence.criteria.Predicate;
import org.crochet.model.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifications {
  public static Specification<Product> searchBy(String text) {
    return (root, query, cb) -> {
      Predicate nameLike = cb.like(root.get("name"), "%" + text + "%");
      Predicate descriptionLike = cb.like(root.get("description"), "%" + text + "%");
      return cb.or(nameLike, descriptionLike);
    };
  }
}
