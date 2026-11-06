package com.infy.sim.simservice.exception;

import com.infy.sim.simservice.dto.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class SimExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");
        return ResponseEntity.badRequest().body(new MessageResponse(message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageResponse> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse("Invalid request");
        return ResponseEntity.badRequest().body(new MessageResponse(message));
    }

    @ExceptionHandler(SimAlreadyActiveException.class)
    public ResponseEntity<MessageResponse> handleAlreadyActive(SimAlreadyActiveException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MessageResponse(exception.getMessage()));
    }

    @ExceptionHandler({ResourceNotFoundException.class, IllegalArgumentException.class})
    public ResponseEntity<MessageResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(exception.getMessage()));
    }
}
