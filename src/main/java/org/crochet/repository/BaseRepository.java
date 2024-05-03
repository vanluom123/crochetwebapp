package org.crochet.repository;

public interface BaseRepository<T> {
    T save(T entity);
    T update(T entity);
    T findById(String id);
    void deleteById(String id);
    void delete(T entity);
}
