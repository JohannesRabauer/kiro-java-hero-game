package com.javahero.exception;

public class PersistenceFailedException extends RuntimeException {

    public PersistenceFailedException(String message) {
        super(message);
    }

    public PersistenceFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
