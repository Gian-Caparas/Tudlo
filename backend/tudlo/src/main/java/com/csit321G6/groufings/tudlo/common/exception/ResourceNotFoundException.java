package com.csit321G6.groufings.tudlo.common.exception;

/** Thrown when a record with the requested id does not exist (HTTP 404). */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entityName, Long id) {
        super(entityName + " with id " + id + " was not found");
    }
}
