package org.crochet.repository;

import jakarta.persistence.criteria.Predicate;
import org.crochet.model.Item;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecifications {
  public static Specification<Item> researchItems(String researchText) {
    return (root, query, cb) -> {
      Predicate nameLike = cb.like(root.get("name"), "%" + researchText + "%");
      Predicate descriptionLike = cb.like(root.get("description"), "%" + researchText + "%");
      return cb.or(nameLike, descriptionLike);
    };
  }
}
