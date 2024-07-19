package org.crochet.repository;

import org.crochet.model.RefreshToken;
import org.crochet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Query("""
            select r
            from RefreshToken r
            where r.user = ?1
            and r.expiresAt > current_timestamp
            and r.revoked = false
            """)
    List<RefreshToken> findAllValidRefreshTokenByUser(User user);
}