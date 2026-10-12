package com.example.employeemanagementsystem.exception;

import lombok.Getter;

/**
 * Thrown when an {@code Employee} cannot be found for the requested id.
 *
 * <p>The message is derived from the id so that callers can rely on it for
 * diagnostics, while {@link #getEmpId()} lets the web layer build its own
 * presentation without parsing that message back apart.
 */
@Getter
public class EmployeeNotFoundException extends RuntimeException {

    private final String empId;

    public EmployeeNotFoundException(String empId) {
        super("Employee not found: " + empId);
        this.empId = empId;
    }
}