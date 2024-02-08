package com.anntz.formservice.exception;

public class FormNotFoundException extends RuntimeException{
    public FormNotFoundException(String message){
        super(message);
    }

    public FormNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
