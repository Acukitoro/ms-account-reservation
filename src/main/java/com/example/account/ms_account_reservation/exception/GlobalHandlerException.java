package com.example.account.ms_account_reservation.exception;

import com.example.account.ms_account_reservation.dto.ErrorDto;
import com.example.account.ms_account_reservation.dto.FieldErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(ClientApiException.class)
    public ResponseEntity<ErrorDto> handleClientApiException(ClientApiException ex) {

        log.warn("Business error: {} - {}", ex.getErrorCode(), ex.getMessage());

        ErrorDto error = new ErrorDto();
        error.setErrorCode(ex.getErrorCode());
        error.setErrorDescription(ex.getMessage());
        error.setStatusCode(ex.getStatus());
        error.setErrors(List.of());

        return ResponseEntity.status(ex.getStatus()).body(error);
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

        ErrorDto error = new ErrorDto();
        error.setErrorCode("VALIDATION_ERROR");
        error.setErrorDescription("Validation field");
        error.setStatusCode(400);
        error.setErrors(fieldErrors);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnknown(Exception ex) {

        log.error("Unexpected error");

        ErrorDto error = new ErrorDto();
        error.setErrorCode("INTERNAL_ERROR");
        error.setErrorDescription("Unexpected error");
        error.setStatusCode(500);
        error.setErrors(List.of());

        return ResponseEntity.status(500).body(error);
    }
}
