package org.crochet.repository;

import org.crochet.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> searchByUserName(String userName) {
        return (root, query, cb) -> {
            return cb.like(root.get("name"), "%" + userName + "%");
        };
    }

    public static Specification<User> searchByEmail(String email) {
        return (root, query, cb) -> {
            return cb.equal(root.get("email"), email);
        };
    }

    public static Specification<User> searchByRole(String role) {
        return (root, query, cb) -> {
            return cb.equal(root.get("role"), role);
        };
    }
}
