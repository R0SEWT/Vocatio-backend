package com.acme.vocatio.web;

import com.acme.vocatio.exception.DuplicateEmailException;
import com.acme.vocatio.exception.InvalidCredentialsException;
import com.acme.vocatio.exception.ProfileNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField, Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Revise los datos enviados");
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolations(ConstraintViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Revise los datos enviados");
        body.put(
                "errors",
                ex.getConstraintViolations().stream()
                        .collect(Collectors.groupingBy(
                                violation -> violation.getPropertyPath().toString(),
                                Collectors.mapping(violation -> violation.getMessage(), Collectors.toList()))));
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(DuplicateEmailException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put(
                "suggestions",
                List.of("Inicia sesión con tu cuenta", "Recupera tu contraseña si la olvidaste"));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProfileNotFound(ProfileNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("suggestions", List.of("Verifique que el usuario exista", "Complete el perfil si es necesario"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
