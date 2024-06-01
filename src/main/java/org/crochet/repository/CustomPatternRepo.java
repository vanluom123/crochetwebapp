package org.crochet.repository;

import org.crochet.model.Pattern;
import org.springframework.stereotype.Repository;

@Repository
public class CustomPatternRepo extends BaseRepositoryImpl<Pattern, String> {
}
