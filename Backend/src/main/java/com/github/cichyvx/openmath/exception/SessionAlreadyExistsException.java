package com.github.cichyvx.openmath.exception;

public class SessionAlreadyExistsException extends RuntimeException {

    public final static String MESSAGE = "Session already exists";

    public SessionAlreadyExistsException() {
        super(MESSAGE);
    }
}
