package org.crochet.repository;

import org.crochet.model.User;
import org.crochet.payload.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    @Query("""
            select new org.crochet.payload.response.UserResponse(
                u.id,
                u.name,
                u.email,
                u.role,
                u.createdDate,
                u.lastModifiedDate
            )
            from User u
            where u.id = ?1
            """)
    Optional<UserResponse> getDetail(String id);
}
