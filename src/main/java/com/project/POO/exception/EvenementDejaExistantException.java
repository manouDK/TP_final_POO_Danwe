package com.project.POO.exception;

public class EvenementDejaExistantException extends RuntimeException {

    public EvenementDejaExistantException(String message) {
        super(message);
    }

    public EvenementDejaExistantException(String message, Throwable cause) {
        super(message, cause);
    }
}