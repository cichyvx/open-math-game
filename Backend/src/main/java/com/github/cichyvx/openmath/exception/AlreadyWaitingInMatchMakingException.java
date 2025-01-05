package com.github.cichyvx.openmath.exception;

public class AlreadyWaitingInMatchMakingException extends RuntimeException {

    public static final String MESSAGE = "Already waiting in MatchMaking";
    public AlreadyWaitingInMatchMakingException() {
        super(MESSAGE);
    }
}
