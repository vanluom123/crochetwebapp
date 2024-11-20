package org.crochet.repository;

import org.crochet.model.Product;
import org.crochet.payload.response.ProductOnHome;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @Query("""
            select new org.crochet.payload.response.ProductOnHome(p.id, p.name, p.description, p.price, p.currencyCode, i.fileContent)
            from Product p
            join p.images i
            where p.isHome = true
                and i.order = 0
            """)
    List<ProductOnHome> findLimitedNumProduct(Pageable pageable);


    @Query("""
            select p
            from Product p
            left join fetch p.images
            where p.id = ?1
            """)
    Optional<Product> getDetail(String id);

    @Query("""
            select new org.crochet.payload.response.ProductOnHome(p.id, p.name, p.description, p.price, p.currencyCode, i.fileContent)
            from Product p
            join p.images i
            where p.id in :ids
                and i.order = 0
            """)
    List<ProductOnHome> findProductOnHomeWithIds(@Param("ids") List<String> ids);
}