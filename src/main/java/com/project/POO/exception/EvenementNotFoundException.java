package com.project.POO.exception;

public class EvenementNotFoundException extends RuntimeException {

    public EvenementNotFoundException(String message) {
        super(message);
    }

    public EvenementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}