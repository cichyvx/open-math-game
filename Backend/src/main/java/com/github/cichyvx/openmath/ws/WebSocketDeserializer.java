package com.github.cichyvx.openmath.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cichyvx.openmath.exception.DeserializationException;
import com.github.cichyvx.openmath.model.request.GenericWsRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;

@Component
public class WebSocketDeserializer {

    private final ObjectMapper objectMapper;

    WebSocketDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    TextMessage mapAsWsResponse(Object object) {
        try {
            return new TextMessage(objectMapper.writeValueAsBytes(object));
        } catch (JsonProcessingException ex) {
            throw new DeserializationException(ex.getMessage());
        }
    }

    GenericWsRequest mapAsWsRequest(byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, GenericWsRequest.class);
        } catch (IOException ex) {
            throw new DeserializationException(ex.getMessage());
        }
    }

    Object convert(Object object, Class<?> type) {
        try {
            return objectMapper.convertValue(object, type);
        } catch (Exception ex) {
            throw new DeserializationException(ex.getMessage());
        }
    }
}
