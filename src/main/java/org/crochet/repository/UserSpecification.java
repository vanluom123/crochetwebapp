package org.crochet.repository;

import org.crochet.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> searchByNameOrEmail(String text) {
        return (root, query, cb) -> {
            var lowerCase = text.toLowerCase();
            var nameLike = cb.like(cb.lower(root.get("name")), "%" + lowerCase + "%");
            var emailLike = cb.like(cb.lower(root.get("email")), "%" + lowerCase + "%");
            return cb.or(nameLike, emailLike);
        };
    }
}
