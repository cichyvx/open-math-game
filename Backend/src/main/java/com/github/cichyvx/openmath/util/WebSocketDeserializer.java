package com.github.cichyvx.openmath.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cichyvx.openmath.exception.DeserializationError;
import com.github.cichyvx.openmath.model.GenericWsRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@Component
public class WebSocketDeserializer {

    private final ObjectMapper objectMapper;

    public WebSocketDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TextMessage mapAsWsResponse(Object object) {
        try {
            return new TextMessage(objectMapper.writeValueAsBytes(object));
        } catch (JsonProcessingException ex) {
            throw new DeserializationError(ex.getMessage());
        }
    }

    public GenericWsRequest mapAsWsRequest(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, GenericWsRequest.class);
        } catch (IOException ex) {
            throw new DeserializationError(ex.getMessage());
        }
    }

    public Object convert(Object object, Class<?> type) {
        try {
            return objectMapper.convertValue(object, type);
        } catch (Exception ex) {
            throw new DeserializationError(ex.getMessage());
        }
    }
}
