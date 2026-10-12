package com.example.employeemanagementsystem.utility;

import com.example.employeemanagementsystem.exception.EmployeeNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ApplicationHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorStructure> handleEmployeeNotFound(EmployeeNotFoundException e) {
        return build(HttpStatus.NOT_FOUND, "Employee with id = " + e.getEmpId() + " not found");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorStructure> handleValidationFailure(MethodArgumentNotValidException e) {
        List<FieldError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorStructure> handleUnreadableBody(HttpMessageNotReadableException e) {
        log.debug("Unreadable request body", e);
        return build(HttpStatus.BAD_REQUEST, "Malformed or unreadable request body");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorStructure> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter '" + e.getName() + "'");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorStructure> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method " + e.getMethod() + " is not supported for this endpoint");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorStructure> handleNoResourceFound(NoResourceFoundException e) {
        return build(HttpStatus.NOT_FOUND, "No endpoint " + e.getResourcePath());
    }

    /**
     * A uniqueness or not-null violation is a conflict with the caller's request, not
     * a server fault. Reported separately from the catch-all so it keeps its 409.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorStructure> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("Data integrity violation", e);
        return build(HttpStatus.CONFLICT, "Request conflicts with existing data");
    }

    /**
     * Last-resort handler. Spring's own exception types implement {@link ErrorResponse}
     * and already carry the correct status for situations this class does not model
     * (415 unsupported media type, 406 not acceptable, 400 missing parameter, ...).
     * They are re-dispatched to their own status rather than being flattened into a
     * 500; only genuinely unrecognised exceptions fall through to the generic branch.
     *
     * <p>The body text is taken from {@link ErrorResponse.ErrorResponse#getDetail()},
     * which Spring populates specifically as a client-safe description. This keeps the
     * project's {@code server.error.include-message: never} posture intact.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorStructure> handleUnexpected(Exception e) {
        if (e instanceof ErrorResponse errorResponse) {
            HttpStatus resolved = HttpStatus.resolve(errorResponse.getStatusCode().value());
            HttpStatus status = resolved != null ? resolved : HttpStatus.INTERNAL_SERVER_ERROR;
            String detail = errorResponse.getBody().getDetail();
            String message = (detail != null && !detail.isBlank())
                    ? detail
                    : status.getReasonPhrase();
            return build(status, message);
        }
        log.error("Unhandled exception", e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<ErrorStructure> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ErrorStructure.of(status, message));
    }

    private ResponseEntity<ErrorStructure> build(HttpStatus status, String message, List<FieldError> errors) {
        return ResponseEntity.status(status).body(ErrorStructure.of(status, message, errors));
    }
}