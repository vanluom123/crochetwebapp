package org.crochet.repository;

import org.crochet.model.FreePattern;
import org.crochet.payload.response.FreePatternOnHome;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreePatternRepository extends JpaRepository<FreePattern, String>, JpaSpecificationExecutor<FreePattern> {

    @Query("""
            select new org.crochet.payload.response.FreePatternOnHome(fp.id, fp.name, fp.description, fp.author, fp.status, i.fileContent)
            from FreePattern fp
            left join fp.images i
            where fp.isHome = true and i.order = 0
            """)
    List<FreePatternOnHome> findLimitedNumFreePattern(Pageable pageable);

    @Query("""
            select f
            from FreePattern f
            join f.colfreps colFrep
            join colFrep.collection c
            where c.user.id = ?1 and f.id = ?2
            """)
    Optional<FreePattern> findFrepInCollection(String userId, String frepId);

    @EntityGraph(attributePaths = {"images", "category"})
    @Query("""
            select f
            from FreePattern f
            where f.id = ?1
            """)
    Optional<FreePattern> getDetail(String id);

    @Query("""
            select new org.crochet.payload.response.FreePatternOnHome(fp.id, fp.name, fp.description, fp.author, fp.status, i.fileContent)
            from FreePattern fp
            left join fp.images i
            where fp.id in :ids
                  and i.order = 0
            """)
    Page<FreePatternOnHome> getFreePatternOnHomeWithIds(@Param("ids") List<String> ids, Pageable pageable);

    @Query("select f.id from FreePattern f order by f.createdDate desc")
    List<String> getFreePatternIds(Pageable pageable);

    @Query("""
            select new org.crochet.payload.response.FreePatternOnHome(fp.id, fp.name, fp.description, fp.author, fp.status, i.fileContent)
            from FreePattern fp
            left join fp.images i
            where fp.createdBy = ?1 and i.order = 0
            """)
    List<FreePatternOnHome> getFrepsByCreateBy(String email);

    @Query("""
            select new org.crochet.payload.response.FreePatternOnHome(fp.id, fp.name, fp.description, fp.author, fp.status, i.fileContent)
            from FreePattern fp
            left join fp.images i
            join fp.colfreps colFrep
            join colFrep.collection c
            where c.user.id = ?1 and c.id = ?2 and i.order = 0
            """)
    List<FreePatternOnHome> getFrepsByCollection(String userId, String collectionId);
}