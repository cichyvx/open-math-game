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
        PathNotFoundException pathNotFoundException = new PathNotFoundException("");
        PathNotSpecifiedException pathNotSpecifiedException = new PathNotSpecifiedException();
        SessionAlreadyExistsException sessionAlreadyExistsException = new SessionAlreadyExistsException();
        DeserializationException deserializationException = new DeserializationException("");
        RuntimeException runtimeException = new RuntimeException();

        return List.of(
                Arguments.of(pathNotFoundException, new ErrorData(ErrorData.PATH_NOT_FOUND, pathNotFoundException.getMessage())),
                Arguments.of(
                        pathNotSpecifiedException, new ErrorData(ErrorData.PARSING_ERROR, pathNotSpecifiedException.getMessage())),
                Arguments.of(sessionAlreadyExistsException, new ErrorData(ErrorData.SESSION_ALREADY_EXISTS, sessionAlreadyExistsException.getMessage())),
                Arguments.of(deserializationException, new ErrorData(ErrorData.DESERIALIZATION_ERROR, ExceptionHandler.UNPROCESSABLE_ENTITY)),
                Arguments.of(runtimeException, new ErrorData(ErrorData.UNEXPECTED_ERROR, ExceptionHandler.UNEXPECTED_EXCEPTION))
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void handleExceptionTest(Exception exception, ErrorData errorData) {
        assertEquals(errorData,subject.handleException(exception));
    }
}
