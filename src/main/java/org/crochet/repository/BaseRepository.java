package org.crochet.repository;

import java.util.UUID;

public interface BaseRepository<T> {
    T save(T entity);
    T update(T entity);
    T findById(UUID id);
    void deleteById(UUID id);
    void delete(T entity);
}
