package org.crochet.repository;

import org.crochet.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingsRepo extends JpaRepository<Settings, String> {
    @Query("select s from Settings s")
    List<Settings> findSettings();
}