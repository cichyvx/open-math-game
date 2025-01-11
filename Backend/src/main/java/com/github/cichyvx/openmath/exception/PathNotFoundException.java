package com.github.cichyvx.openmath.exception;

public class PathNotFoundException extends RuntimeException {

    public final static String MESSAGE = "path %s not found";

    public PathNotFoundException(String path) {
        super(MESSAGE.formatted(path));
    }
}
