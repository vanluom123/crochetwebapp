package org.crochet.repository;

import org.crochet.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    @Query("""
            select p from Product p
            join Category c on c.id = p.category.id
            where p.category.id = ?1
            """)
    List<Product> findProductByCategory(UUID categoryId);
}