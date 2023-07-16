package org.crochet.repository;

import org.crochet.model.ConfirmationToken;
import org.crochet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
  Optional<ConfirmationToken> findByUser(User user);

  Optional<ConfirmationToken> findByToken(String token);

  @Query(value = "SELECT c.id, c.token, c.created_at, c.expires_at, c.confirmed_at, c.user_id " +
      "FROM confirmation_token c " +
      "JOIN users u ON c.user_id = u.id " +
      "WHERE u.email = ?1", nativeQuery = true)
  Optional<ConfirmationToken> findByEmail(String email);

  @Transactional
  @Modifying
  @Query("UPDATE ConfirmationToken c " +
      "SET c.confirmedAt = ?2 " +
      "WHERE c.token = ?1")
  void updateConfirmedAt(String token, LocalDateTime confirmedAt);
}