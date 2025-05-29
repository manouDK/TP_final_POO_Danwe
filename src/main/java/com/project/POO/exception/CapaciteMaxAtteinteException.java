package com.project.POO.exception;

public class CapaciteMaxAtteinteException extends RuntimeException {

    public CapaciteMaxAtteinteException(String message) {
        super(message);
    }

    public CapaciteMaxAtteinteException(String message, Throwable cause) {
        super(message, cause);
    }
}