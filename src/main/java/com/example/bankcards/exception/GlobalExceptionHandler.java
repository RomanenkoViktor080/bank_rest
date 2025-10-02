package com.example.bankcards.exception;

import com.example.bankcards.dto.ErrorResponse;
import com.example.bankcards.exception.api.ApiException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    public static final String UNKNOWN_FIELD = "unknown field";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.unprocessableEntity().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getCause();

        if (rootCause instanceof InvalidFormatException invalid) {
            String field = extractField(invalid.getPath());
            String message = "Invalid format for field '" + field + "'";
            log.error(message);
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(message));
        }
        log.error("Unreadable HTTP message: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("Malformed JSON request"));
    }

    private static String extractField(List<JsonMappingException.Reference> path) {
        return (path == null || path.isEmpty())
                ? UNKNOWN_FIELD
                : path.get(path.size() - 1).getFieldName();
    }

    @ExceptionHandler(value = {BadCredentialsException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorResponse> handleApiException(Exception ex) {
        log.error("message={}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest req) {
        log.error("message={}", ex.getDebugMessage(), ex);
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleApiException(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception, message={}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Server Error"));
    }

}

