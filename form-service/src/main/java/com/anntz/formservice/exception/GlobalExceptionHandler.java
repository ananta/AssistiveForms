package com.anntz.formservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({FormNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleFormNotFoundException(FormNotFoundException ex) {
        ApiError apiError =  new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(),apiError.getStatus());
    }
}
