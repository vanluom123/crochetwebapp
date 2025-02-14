package org.crochet.repository;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.crochet.model.Category;
import org.crochet.model.FreePattern;
import org.springframework.data.jpa.domain.Specification;

@SuppressWarnings({"DataFlowIssue", "unused"})
public class FreePatternSpecifications {

    public static Specification<FreePattern> fetchJoin() {
        return (r, q, cb) -> {
            if (Long.class != q.getResultType()) {
                r.fetch("files", JoinType.LEFT);
                r.fetch("images", JoinType.LEFT);
            }
            q.distinct(true);
            return cb.conjunction();
        };
    }

    public static Specification<FreePattern> getAllByCategoryId(String categoryId) {
        return (root, query, cb) -> {
            Subquery<String> subquery = query.subquery(String.class);
            Root<Category> subRoot = subquery.from(Category.class);

            Predicate or = cb.or(cb.equal(subRoot.get("id"), categoryId),
                    cb.equal(subRoot.get("parent").get("id"), categoryId));
            subquery.select(subRoot.get("id"))
                    .where(or);

            query.distinct(true);

            return cb.in(root.get("category").get("id")).value(subquery);
        };
    }
}
