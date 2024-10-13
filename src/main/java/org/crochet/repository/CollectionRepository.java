package org.crochet.repository;

import org.crochet.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, String> {

    @Query("SELECT DISTINCT c FROM Collection c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.savingCharts")
    List<Collection> findAllWithUserAndSavingCharts();

    @Query("SELECT DISTINCT c FROM Collection c LEFT JOIN FETCH c.savingCharts WHERE c.user.id = :userId")
    List<Collection> findAllByUserIdWithSavingCharts(@Param("userId") String userId);
}
