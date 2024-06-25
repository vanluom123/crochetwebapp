package org.crochet.repository;

import org.crochet.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @Query("""
            select p
            from Product p
            left join fetch p.images
            where p.isHome = true
            order by p.createdDate desc
            limit ?1
            """)
    List<Product> findLimitedNumProductByCreatedDateDesc(int limited);

    @Query("""
            select prod
            from Product prod
            join fetch prod.category c
            where c.id = ?1
            """)
    List<Product> findProductByCategory(String categoryId);

    @Query("""
            select p
            from Product p
            left join fetch p.images
            where p.id = ?1
            """)
    Optional<Product> getDetail(String id);
}