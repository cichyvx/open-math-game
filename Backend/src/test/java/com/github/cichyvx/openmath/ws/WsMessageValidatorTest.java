package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.exception.PathNotSpecified;
import com.github.cichyvx.openmath.model.request.GenericWsRequest;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WsMessageValidatorTest {

    private final WsMessageValidator subject = new WsMessageValidator();

    @ParameterizedTest
    @CsvSource(value = {"valid,true", "null,false", "'',false"}, nullValues = "null")
    public void validateTest(String path, boolean isValid) {
        Executable executable = () -> subject.validate(new GenericWsRequest(path, null));
        if (!isValid) {
            assertThrows(PathNotSpecified.class, executable);
        } else {
            assertDoesNotThrow(executable);
        }
    }

}
