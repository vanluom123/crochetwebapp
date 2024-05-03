package org.crochet.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.crochet.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseRepositoryImpl<T> implements BaseRepository<T> {
    @PersistenceContext
    protected EntityManager em;

    private final Class<T> type;

    public BaseRepositoryImpl(Class<T> type) {
        this.type = type;
    }

    @Transactional
    public T save(T entity) {
        em.persist(entity);
        return entity;
    }

    @Transactional
    public T update(T entity) {
        return em.merge(entity);
    }

    public T findById(String id) {
        T entity = em.find(type, id);
        if (entity == null) {
            throw new ResourceNotFoundException(type.getSimpleName() + " not found");
        }
        return entity;
    }

    @Transactional
    public void deleteById(String id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }

    @Transactional
    public void delete(T entity) {
        em.remove(entity);
    }
}
