package com.anntz.formservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Getter
public class ApiError{
    final private int status;
    final private String message;
    final private List<String> errors;

    public ApiError(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status.value();
        this.message = message;
        this.errors = errors;
    }

    public ApiError(HttpStatus status, String message) {
        super();
        this.status = status.value();
        this.message = message;
        this.errors = Collections.emptyList();
    }

}
