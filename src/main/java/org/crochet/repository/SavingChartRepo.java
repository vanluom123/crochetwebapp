package org.crochet.repository;

import org.crochet.model.SavingChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SavingChartRepo extends JpaRepository<SavingChart, String>, JpaSpecificationExecutor<SavingChart> {

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
                    :freePatternId,
                    :userId)
            """, nativeQuery = true)
    void createSavingChart(@Param("freePatternId") String freePatternId, @Param("userId") String userId);

    boolean existsByUserIdAndFreePatternId(String userId, String freePatternId);

    @Transactional
    @Modifying
    @Query("""
                delete from SavingChart sc
                where sc.user.id = :userId and sc.freePattern.id = :freePatternId
            """)
    void deleteByUserIdAndFreePatternId(@Param("userId") String userId,
                                        @Param("freePatternId") String freePatternId);
}
