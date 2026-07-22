package com.example.account.ms_account_reservation.exception;

import com.example.account.ms_account_reservation.dto.ErrorDto;
import com.example.account.ms_account_reservation.dto.FieldErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(ClientApiException.class)
    public ResponseEntity<ErrorDto> handleClientApiException(ClientApiException ex) {

        log.warn("Business error: {} - {}", ex.getErrorCode(), ex.getMessage());
        
        return build(ex.getErrorCode(), ex.getMessage(), ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidation(MethodArgumentNotValidException ex) {

        List<FieldErrorDto> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> {
                    FieldErrorDto dto = new FieldErrorDto();
                    dto.setField(err.getField());
                    dto.setMessage(err.getDefaultMessage());
                    return dto;
                })
                .toList();

        log.warn("Validation error: {}", fieldErrors);

        return build("VALIDATION_ERROR",  "Validation field", 400, fieldErrors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnknown(Exception ex) {

        log.error("Unexpected error", ex);

        return build("INTERNAL_ERROR", "Unexpected error", 500);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorDto> handleBadRequest(Exception ex) {

        log.warn("Bad request: {}", ex.getMessage());

        return build("BAD_REQUEST", "Malformed or invalid request", 400);
    }

    @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<ErrorDto> handleNotFound(NoResourceFoundException ex) {

        log.warn("No handler: {}", ex.getMessage());

        return build("NOT_FOUND", "Resource not found", 404);
    }

    private ResponseEntity<ErrorDto> build(String errorCode, String description, int statusCode) {
        return build(errorCode, description, statusCode, List.of());
    }

    private ResponseEntity<ErrorDto> build(String errorCode, String description, int statusCode, List<FieldErrorDto> errors) {

        ErrorDto error = new ErrorDto();
        error.setErrorCode(errorCode);
        error.setErrorDescription(description);
        error.setStatusCode(statusCode);
        error.setErrors(errors);

        return ResponseEntity.status(statusCode).body(error);
    }
}
