package com.csit321G6.groufings.tudlo.common.exception;

/** Thrown when a request breaks a business rule (HTTP 400). */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
