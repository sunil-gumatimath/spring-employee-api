package com.example.employeemanagementsystem.utility;

/**
 * A single field-level validation failure.
 */
public record FieldError(String field, Object rejectedValue, String message) {
}