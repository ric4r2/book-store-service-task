package com.epam.rd.autocode.spring.project.exception;

public class AlreadyExistException extends RuntimeException {

    public AlreadyExistException(String message) {
        super(message);
    }

    public AlreadyExistException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }

    public AlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
