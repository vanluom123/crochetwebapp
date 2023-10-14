package org.crochet.repository;

import org.crochet.model.Pattern;
import org.springframework.data.jpa.domain.Specification;

public class PatternSpecifications {
  public static Specification<Pattern> searchBy(String text) {
    return (r, q, cb) -> {
      var name = cb.like(r.get("name"), "%" + text + "%");
      var desc = cb.like(r.get("description"), "%" + text + "%");
      return cb.or(name, desc);
    };
  }
}
