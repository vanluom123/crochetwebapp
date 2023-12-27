package org.crochet.repository;

import org.crochet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  @Transactional
  @Modifying
  @Query("UPDATE User user " +
      "SET user.emailVerified = TRUE WHERE user.email = ?1")
  void verifyEmail(String email);

  @Transactional
  @Modifying
  @Query("UPDATE User u SET u.password = ?1 WHERE u.email = ?2")
  void updatePassword(String password, String email);
}
