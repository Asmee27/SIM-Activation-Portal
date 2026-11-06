package com.infy.sim.customerservice.exception;

import com.infy.sim.customerservice.dto.MessageResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.json.JsonMapper;

@RestControllerAdvice
public class CustomerExceptionHandler {

    private final JsonMapper jsonMapper;

    public CustomerExceptionHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");
        return ResponseEntity.badRequest().body(new MessageResponse(message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageResponse> handleUnreadable(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid request format"));
    }

    @ExceptionHandler(InvalidDetailsException.class)
    public ResponseEntity<MessageResponse> handleInvalidDetails(InvalidDetailsException exception) {
        return ResponseEntity.badRequest().body(new MessageResponse(exception.getMessage()));
    }

    @ExceptionHandler(NoRequestFoundException.class)
    public ResponseEntity<MessageResponse> handleNoRequest(NoRequestFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(exception.getMessage()));
    }

    /**
     * sim-service answered with an error (e.g. 409 "SIM already active"): pass its
     * status and message through. Status -1 means it could not be reached at all.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<MessageResponse> handleFeign(FeignException exception) {
        if (exception.status() <= 0) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new MessageResponse("SIM service is unavailable. Please try again later."));
        }
        String message = "SIM activation failed";
        try {
            MessageResponse body = jsonMapper.readValue(exception.contentUTF8(), MessageResponse.class);
            if (body.message() != null) {
                message = body.message();
            }
        } catch (RuntimeException ignored) {
            // body was not a MessageResponse; keep the generic message
        }
        return ResponseEntity.status(exception.status()).body(new MessageResponse(message));
    }
}
