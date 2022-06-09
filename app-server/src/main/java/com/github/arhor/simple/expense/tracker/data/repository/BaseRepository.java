package com.github.arhor.simple.expense.tracker.data.repository;

import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.github.arhor.simple.expense.tracker.data.model.DomainObject;

@NoRepositoryBean
public interface BaseRepository<T extends DomainObject<K>, K extends Serializable> extends CrudRepository<T, K> {

    @Override
    default void delete(final T entity) {
        var id = entity.getId();
        if (id != null) {
            deleteById(id);
        }
    }

    @Override
    default void deleteAll(final Iterable<? extends T> entities) {
        var ids = new ArrayList<K>();
        for (var entity : entities) {
            if (entity != null) {
                var id = entity.getId();
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        if (!ids.isEmpty()) {
            deleteAllById(ids);
        }
    }
}
