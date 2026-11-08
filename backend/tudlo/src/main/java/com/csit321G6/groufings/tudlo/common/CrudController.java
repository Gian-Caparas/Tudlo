package com.csit321G6.groufings.tudlo.common;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Standard REST endpoints shared by every entity:
 * GET (all), GET (one), POST, PUT, DELETE.
 */
public abstract class CrudController<Req, Res> {

    protected abstract CrudService<?, Req, Res> service();

    @GetMapping
    public List<Res> getAll() {
        return service().findAll();
    }

    @GetMapping("/{id}")
    public Res getById(@PathVariable("id") Long id) {
        return service().findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Res create(@Valid @RequestBody Req request) {
        return service().create(request);
    }

    @PutMapping("/{id}")
    public Res update(@PathVariable("id") Long id, @Valid @RequestBody Req request) {
        return service().update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        service().delete(id);
    }
}
