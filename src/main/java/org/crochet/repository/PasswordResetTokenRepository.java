package org.crochet.repository;

import org.crochet.model.PasswordResetToken;
import org.crochet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByUser(User user);

    Optional<PasswordResetToken> findByToken(String token);

    @Query("""
            select p.user
            from PasswordResetToken p
            where p.token = ?1
            """)
    Optional<User> getUserByToken(String token);


    @Query("""
            select u.email
            from PasswordResetToken p
            join User u on u.id = p.user.id
            where p.token = ?1
            """)
    Optional<String> findEmailByToken(String token);
}