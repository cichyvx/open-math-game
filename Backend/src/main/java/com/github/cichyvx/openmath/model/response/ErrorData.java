package com.github.cichyvx.openmath.model.response;

public record ErrorData(int errorCode, String errorMessage) {

    public static int PARSING_ERROR = 0,
                      SESSION_ALREADY_EXISTS = 1,
                      PATH_NOT_FOUND = 2,
                      UNEXPECTED_ERROR = 3,
                      DESERIALIZATION_ERROR = 4;
}
