package org.crochet.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.crochet.exception.ResourceNotFoundException;
import org.crochet.model.BaseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

public class BaseRepositoryImpl<T extends BaseEntity, ID extends Serializable> implements BaseRepository<T, ID> {
    @PersistenceContext
    private EntityManager em;

    private final Class<T> entityClass;

    public BaseRepositoryImpl() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Transactional
    @Override
    public T save(T entity) {
        em.persist(entity);
        return entity;
    }

    @Transactional
    @Override
    public T update(T entity) {
        return em.merge(entity);
    }

    @Override
    public T findById(ID id) {
        if (id == null) {
            return null;
        }
        var entity = em.find(entityClass, id);
        if (entity == null) {
            throw new ResourceNotFoundException(entityClass.getSimpleName() + " not found");
        }
        return entity;
    }

    @Transactional
    @Override
    public void deleteById(ID id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }

    @Transactional
    @Override
    public void delete(T entity) {
        em.remove(entity);
    }
}
