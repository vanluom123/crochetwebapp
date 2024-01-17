package org.crochet.repository;

import org.crochet.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepo extends JpaRepository<Token, UUID> {
  Optional<Token> findByToken(String token);
  @Query(value = """
      select t from Token t inner join User u
      on t.user.id = u.id
      where u.id = ?1 and (t.expired = false or t.revoked = false)
      """)
  List<Token> findAllValidTokenByUser(UUID id);
}