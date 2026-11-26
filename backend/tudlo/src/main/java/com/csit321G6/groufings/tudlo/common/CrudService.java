package com.csit321G6.groufings.tudlo.common;

import com.csit321G6.groufings.tudlo.common.exception.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Shared CRUD logic. Each entity's service only describes how to build an entity
 * from a request ({@link #apply}) and how to turn an entity into a response
 * ({@link #toResponse}).
 *
 * @param <E>   entity type
 * @param <Req> request DTO type
 * @param <Res> response DTO type
 */
@SuppressWarnings("null")
public abstract class CrudService<E, Req, Res> {

    protected abstract JpaRepository<E, Long> repository();

    protected abstract String entityName();

    protected abstract E newEntity();

    /** Copies the request values (and referenced records) into the entity. */
    protected abstract void apply(E entity, Req request);

    protected abstract Res toResponse(E entity);

    @Transactional(readOnly = true)
    public List<Res> findAll() {
        return filterForRead(repository().findAll()).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Res findById(Long id) {
        E entity = getEntity(id);
        beforeRead(entity);
        return toResponse(entity);
    }

    @Transactional
    public Res create(Req request) {
        E entity = newEntity();
        beforeCreate(entity, request);
        apply(entity, request);
        E saved = repository().saveAndFlush(entity);
        afterCreate(saved, request);
        return toResponse(saved);
    }

    @Transactional
    public Res update(Long id, Req request) {
        E entity = getEntity(id);
        beforeUpdate(entity, request);
        apply(entity, request);
        E saved = repository().saveAndFlush(entity);
        afterUpdate(saved, request);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        E entity = getEntity(id);
        beforeDelete(entity);
        repository().delete(entity);
        repository().flush();
        afterDelete(entity);
    }

    protected List<E> filterForRead(List<E> entities) {
        return entities;
    }

    protected void beforeRead(E entity) {
    }

    protected void beforeCreate(E entity, Req request) {
    }

    protected void beforeUpdate(E existing, Req request) {
    }

    protected void beforeDelete(E existing) {
    }

    protected void afterCreate(E created, Req request) {
    }

    protected void afterUpdate(E updated, Req request) {
    }

    protected void afterDelete(E deleted) {
    }

    protected E getEntity(Long id) {
        return repository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName(), id));
    }

    /** Looks up a record referenced by id in a request body. */
    protected <T> T find(JpaRepository<T, Long> repository, Long id, String name) {
        return repository.findById(id)
                .orElseThrow(() -> new BadRequestException(name + " with id " + id + " does not exist"));
    }
}