package org.crochet.repository;

import org.crochet.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class CustomUserRepo extends BaseRepositoryImpl<User, String> {
}
