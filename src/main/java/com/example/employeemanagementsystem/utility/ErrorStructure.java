package com.example.employeemanagementsystem.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Error envelope. Deliberately shares the {@code status} and {@code message} shape with
 * {@link ResponseStructure} so clients can parse both response kinds with one contract;
 * {@code errors} is populated only for field-level validation failures.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorStructure(
        int status,
        String message,
        List<FieldError> errors
) {

    public static ErrorStructure of(HttpStatus status, String message) {
        return new ErrorStructure(status.value(), message, null);
    }

    public static ErrorStructure of(HttpStatus status, String message, List<FieldError> errors) {
        return new ErrorStructure(status.value(), message, errors);
    }
}