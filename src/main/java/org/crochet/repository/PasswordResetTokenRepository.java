package org.crochet.repository;

import org.crochet.model.PasswordResetToken;
import org.crochet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
  Optional<PasswordResetToken> findByUser(User user);

  Optional<PasswordResetToken> findByToken(String token);

  @Query("select p.user " +
      "from PasswordResetToken p " +
      "join fetch User u on p.user.id = u.id " +
      "where p.token = ?1")
  Optional<User> getUserByToken(String token);
}