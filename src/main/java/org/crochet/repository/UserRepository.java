package org.crochet.repository;

import org.crochet.model.User;
import org.crochet.payload.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("""
            update User u
            set u.emailVerified = true
            where u.email = ?1
            """)
    void verifyEmail(String email);

    @Transactional
    @Modifying
    @Query("""
            update User u
            set u.password = ?1
            where u.email = ?2
            """)
    void updatePassword(String password, String email);

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
