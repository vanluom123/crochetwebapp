package org.crochet.repository;

import org.crochet.model.SavingChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SavingChartRepo extends JpaRepository<SavingChart, String>, JpaSpecificationExecutor<SavingChart> {

    boolean existsByUserIdAndFreePatternId(String userId, String freePatternId);

    @Transactional
    @Modifying
    @Query("""
                delete from SavingChart sc
                where sc.user.id = :userId and sc.freePattern.id = :freePatternId
            """)
    void deleteByUserIdAndFreePatternId(@Param("userId") String userId,
                                        @Param("freePatternId") String freePatternId);

    @Query("""
            select (count(s.id) > 0)
            from SavingChart s
            left join s.collection c on s.collection.id = c.id
            where c.name like concat('%', :name, '%') and s.id IN :ids
            """)
    boolean existsSavingChartByCollectionNameContains(@Param("ids") List<String> ids, @Param("name") String name);
}
