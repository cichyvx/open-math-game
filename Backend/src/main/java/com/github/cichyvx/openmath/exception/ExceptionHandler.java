package com.github.cichyvx.openmath.exception;

import com.github.cichyvx.openmath.model.response.ErrorData;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {

    public static final String UNPROCESSABLE_ENTITY = "unprocessable entity";
    public static final String UNEXPECTED_EXCEPTION = "unexpected exception";

    public ErrorData handleException(final Exception exception) {
        return switch (exception) {
            case PathNotFoundException ex -> new ErrorData(ErrorData.PATH_NOT_FOUND, ex.getMessage());
            case PathNotSpecifiedException ex -> new ErrorData(ErrorData.PARSING_ERROR, ex.getMessage());
            case SessionAlreadyExistsException ex -> new ErrorData(ErrorData.SESSION_ALREADY_EXISTS, ex.getMessage());
            case DeserializationException ex -> new ErrorData(ErrorData.DESERIALIZATION_ERROR, UNPROCESSABLE_ENTITY);
            default -> new ErrorData(ErrorData.UNEXPECTED_ERROR, UNEXPECTED_EXCEPTION);
        };
    }

}
