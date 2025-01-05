package com.github.cichyvx.openmath.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cichyvx.openmath.exception.DeserializationError;
import com.github.cichyvx.openmath.model.GenericWsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebSocketDeserializerTest {

    public static final byte[] BYTES = {};
    private WebSocketDeserializer subject;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = mock(ObjectMapper.class);
        subject = new WebSocketDeserializer(objectMapper);
    }

    @Test
    public void mapAsWsResponseTest() throws JsonProcessingException {
        when(objectMapper.writeValueAsBytes(any())).thenReturn(BYTES);

        var result = subject.mapAsWsResponse(any());

        assertNotNull(result);
    }

    @Test
    public void mapAsWsResponseThrowTest() throws JsonProcessingException {
        when(objectMapper.writeValueAsBytes(any())).thenThrow(JsonProcessingException.class);

        assertThrows(DeserializationError.class, () -> subject.mapAsWsResponse(any()));
    }

    @Test
    public void mapAsWsRequestTest() throws IOException {
        when(objectMapper.readValue(any(byte[].class), any(Class.class))).thenReturn(mock(GenericWsRequest.class));

        var result = subject.mapAsWsRequest(BYTES);

        assertNotNull(result);
    }

    @Test
    public void mapAsWsRequestThrowTest() throws IOException {
        when(objectMapper.readValue(any(byte[].class), any(Class.class))).thenThrow(IOException.class);

        assertThrows(DeserializationError.class, () -> subject.mapAsWsRequest(BYTES));
    }

    @Test
    public void convertTest() {
        when(objectMapper.convertValue(any(Object.class), any(Class.class))).thenReturn(new Object());

        var result = subject.convert(new Object(), Class.class);

        assertNotNull(result);
    }

    @Test
    public void convertThrowTest() {
        when(objectMapper.convertValue(any(Object.class), any(Class.class))).thenThrow(RuntimeException.class);

        assertThrows(DeserializationError.class, () -> subject.convert(new Object(), Class.class));
    }
}
