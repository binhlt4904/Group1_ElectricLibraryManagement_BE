package com.library.librarymanagement.exception;

public class ObjectNotExistException extends RuntimeException {
    public ObjectNotExistException(String message) {
        super(message);
    }
}
