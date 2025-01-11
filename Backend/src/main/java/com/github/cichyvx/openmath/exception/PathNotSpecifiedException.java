package com.github.cichyvx.openmath.exception;

public class PathNotSpecifiedException extends RuntimeException {

    public final static String MESSAGE = "Path not specified!";

    public PathNotSpecifiedException() {
        super(MESSAGE);
    }
}
