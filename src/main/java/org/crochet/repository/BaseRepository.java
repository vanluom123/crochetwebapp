package org.crochet.repository;

import org.crochet.model.BaseEntity;

import java.io.Serializable;

public interface BaseRepository<T extends BaseEntity, ID extends Serializable> {
    T save(T entity);
    T update(T entity);
    T findById(ID id);
    void deleteById(ID id);
    void delete(T entity);
}
