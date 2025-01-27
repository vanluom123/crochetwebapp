package org.crochet.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.crochet.model.FreePattern;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FreePatternRepoImpl implements FreePatternRepoCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<String> findAllIds(Specification<FreePattern> spec) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<FreePattern> root = query.from(FreePattern.class);

        query.select(root.get("id"));

        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, query, cb);
            if (predicate != null) {
                query.where(predicate);
            }
        }

        return em.createQuery(query).getResultList();
    }
}
