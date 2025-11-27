package com.example.demo.lee.RuntimeException;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        ApiError apiError = new ApiError("LEE_NOT_FOUND", ex.getMessage(), "warning");
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex) {
        ApiError apiError = new ApiError("LEE_VALID_FAIL", ex.getMessage(), "warning");
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // 数据库重复数据
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Object> handleDuplicateException(DuplicateException ex) {
        ApiError apiError = new ApiError("LEE_DATA_DUPLICATE", ex.getMessage(), "warning");
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @Setter
    @Getter
    private static class ApiError {
        private String errorCode;
        private String message;
        private String type;

        public ApiError(String errorCode, String message, String type) {
            this.errorCode = errorCode;
            this.message = message;
            this.type = type;
        }
    }
}