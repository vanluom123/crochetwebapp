package org.crochet.repository;

import org.crochet.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> searchByNameOrEmail(String text) {
        return (root, query, cb) -> {
            var nameLike = cb.like(root.get("name"), "%" + text + "%");
            var emailLike = cb.like(root.get("email"), "%" + text + "%");
            return cb.or(nameLike, emailLike);
        };
    }
}
