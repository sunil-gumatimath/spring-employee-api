package com.example.employeemanagementsystem.utility;

/**
 * Success envelope. Deliberately shares the {@code status}/{@code message} shape with
 * {@link ErrorStructure} so clients can parse both response kinds with one contract.
 */
public record ResponseStructure<T>(
        int status,
        String message,
        T data
) {

    public static <T> ResponseStructure<T> of(int status, String message, T data) {
        return new ResponseStructure<>(status, message, data);
    }
}