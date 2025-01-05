package com.github.cichyvx.openmath.exception;

public class PathNotSpecified extends RuntimeException {

    public final static String MESSAGE = "Path not specified!";

    public PathNotSpecified() {
        super(MESSAGE);
    }
}
