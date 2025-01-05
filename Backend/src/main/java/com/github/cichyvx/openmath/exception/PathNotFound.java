package com.github.cichyvx.openmath.exception;

public class PathNotFound extends RuntimeException {

    public final static String MESSAGE = "path %s not found";

    public PathNotFound(String path) {
        super(MESSAGE.formatted(path));
    }
}
