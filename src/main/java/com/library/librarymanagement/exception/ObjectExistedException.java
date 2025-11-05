package com.library.librarymanagement.exception;

public class ObjectExistedException extends RuntimeException {
    public ObjectExistedException(String message) {
        super(message);
    }
}
