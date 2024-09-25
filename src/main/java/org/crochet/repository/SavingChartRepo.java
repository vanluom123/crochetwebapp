package org.crochet.repository;

import org.crochet.model.SavingChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SavingChartRepo extends JpaRepository<SavingChart, String> {

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO saving_chart (id,
                                      created_date,
                                      last_modified_date,
                                      free_pattern_id,
                                      user_id)
            VALUES (uuid(),
                    current_timestamp,
                    current_timestamp,
                    ?1,
                    ?2)
            """, nativeQuery = true)
    void createSavingChart(String freePatternId, String userId);

    boolean existsByUserIdAndFreePatternId(String userId, String freePatternId);

    @Transactional
    @Modifying
    @Query("""
                delete from SavingChart sc
                where sc.user.id = ?1 and sc.freePattern.id = ?2
            """)
    void deleteByUserIdAndFreePatternId(String userId,
                                        String freePatternId);
}
