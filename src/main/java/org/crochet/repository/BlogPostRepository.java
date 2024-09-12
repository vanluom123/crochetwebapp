package org.crochet.repository;

import org.crochet.model.BlogPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, String>, JpaSpecificationExecutor<BlogPost> {

    @Query("""
            select p
            from BlogPost p
            where p.home = true
            """)
    List<BlogPost> findLimitedNumPosts(Pageable pageable);

    @Query("""
            select p
            from BlogPost p
            left join fetch p.files
            where p.id = ?1
            """)
    Optional<BlogPost> getDetail(String id);
}