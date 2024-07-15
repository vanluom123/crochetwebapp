package org.crochet.repository;

import org.crochet.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SettingsRepo extends JpaRepository<Settings, String> {
    @Query("select s.value from Settings s where s.key = ?1")
    Optional<String> findByKey(String key);
}