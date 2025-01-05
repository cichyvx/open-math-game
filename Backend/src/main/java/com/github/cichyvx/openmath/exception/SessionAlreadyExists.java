package com.github.cichyvx.openmath.exception;

public class SessionAlreadyExists extends RuntimeException {

    public final static String MESSAGE = "Session already exists";

    public SessionAlreadyExists() {
        super(MESSAGE);
    }
}
