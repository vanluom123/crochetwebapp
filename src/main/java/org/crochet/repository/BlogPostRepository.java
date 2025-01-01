package org.crochet.repository;

import org.crochet.model.BlogPost;
import org.crochet.payload.response.BlogOnHome;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, String>, JpaSpecificationExecutor<BlogPost> {

    @Query("""
            select new org.crochet.payload.response.BlogOnHome(p.id, p.title, p.content, f.fileContent, p.createdDate)
            from BlogPost p
            left join p.files f
            where p.home = true
                and f.order = 0
            """)
    List<BlogOnHome> findLimitedNumPosts(Pageable pageable);

    @Query("""
            select p
            from BlogPost p
            left join fetch p.files
            where p.id = ?1
            """)
    Optional<BlogPost> getDetail(String id);

    @Query("""
            select new org.crochet.payload.response.BlogOnHome(p.id, p.title, p.content, f.fileContent, p.createdDate)
            from BlogPost p
            left join p.files f
            where p.id in :ids
                and f.order = 0
            """)
    Page<BlogOnHome> findBlogOnHomeWithIds(@Param("ids") List<String> ids, Pageable pageable);

    @Query("""
            select p.id
            from BlogPost p
            order by p.createdDate desc
            """)
    List<String> getBlogIds(Pageable pageable);
}
