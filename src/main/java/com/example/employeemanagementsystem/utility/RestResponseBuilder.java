package com.example.employeemanagementsystem.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class RestResponseBuilder {

    private RestResponseBuilder() {
    }

    /**
     * Builds a successful response where the HTTP status and the status echoed in the
     * body are guaranteed to come from the same {@link HttpStatus} value.
     */
    public static <T> ResponseEntity<ResponseStructure<T>> build(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(ResponseStructure.of(status.value(), message, data));
    }
}