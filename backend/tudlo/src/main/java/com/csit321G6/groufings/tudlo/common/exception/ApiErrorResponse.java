package com.csit321G6.groufings.tudlo.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        String path,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<String, String> fieldErrors
) {
    public ApiErrorResponse(int status, String error, String message, Instant timestamp, String path) {
        this(status, error, message, timestamp, path, null);
    }
}
