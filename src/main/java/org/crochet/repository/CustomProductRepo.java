package org.crochet.repository;

import org.crochet.model.Product;
import org.springframework.stereotype.Repository;

@Repository
public class CustomProductRepo extends BaseRepositoryImpl<Product, String> {
}
