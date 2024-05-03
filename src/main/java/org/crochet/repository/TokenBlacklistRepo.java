package org.crochet.repository;

import org.crochet.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenBlacklistRepo extends JpaRepository<TokenBlacklist, String> {
    Optional<TokenBlacklist> findByToken(String token);
}