package com.github.cichyvx.openmath.exception;

import com.github.cichyvx.openmath.model.response.ErrorData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExceptionHandlerTest {

    private final ExceptionHandler subject = new ExceptionHandler();

    static List<Arguments> data() {
        PathNotFound pathNotFound = new PathNotFound("");
        PathNotSpecified pathNotSpecified = new PathNotSpecified();
        SessionAlreadyExists sessionAlreadyExists = new SessionAlreadyExists();
        DeserializationError deserializationError = new DeserializationError("");
        RuntimeException runtimeException = new RuntimeException();

        return List.of(
                Arguments.of(pathNotFound, new ErrorData(ErrorData.PATH_NOT_FOUND, pathNotFound.getMessage())),
                Arguments.of(pathNotSpecified, new ErrorData(ErrorData.PARSING_ERROR, pathNotSpecified.getMessage())),
                Arguments.of(sessionAlreadyExists, new ErrorData(ErrorData.SESSION_ALREADY_EXISTS, sessionAlreadyExists.getMessage())),
                Arguments.of(deserializationError, new ErrorData(ErrorData.DESERIALIZATION_ERROR, ExceptionHandler.UNPROCESSABLE_ENTITY)),
                Arguments.of(runtimeException, new ErrorData(ErrorData.UNEXPECTED_ERROR, ExceptionHandler.UNEXPECTED_EXCEPTION))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void handleExceptionTest(Exception exception, ErrorData errorData) {
        assertEquals(errorData,subject.handleException(exception));
    }
}
