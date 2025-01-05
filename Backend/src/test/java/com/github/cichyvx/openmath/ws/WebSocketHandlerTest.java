package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.model.ErrorData;
import com.github.cichyvx.openmath.util.WebSocketDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebSocketHandlerTest {

    private WebSocketHandler subject;
    private WebSocketMessagePathMapper pathMapper;
    private WebSocketDeserializer webSocketDeserializer;

    @BeforeEach
    public void setUp() {
        pathMapper = mock(WebSocketMessagePathMapper.class);
        webSocketDeserializer = mock(WebSocketDeserializer.class);

        subject = new WebSocketHandler(pathMapper, webSocketDeserializer);
    }

    @Test
    public void shouldCallPathMapperAndNotSendError() throws IOException {
        WebSocketSession session = mock(WebSocketSession.class);
        TextMessage message = mock(TextMessage.class);

        subject.handleTextMessage(session, message);

        verify(pathMapper, times(1)).processMessage(eq(session), eq(message));
        verify(session, times(0)).sendMessage(any());
        verify(webSocketDeserializer, times(0)).mapAsWsResponse(any());
    }

    @Test
    public void shouldCallPathMapperAndSendError() throws IOException {
        WebSocketSession session = mock(WebSocketSession.class);
        TextMessage message = mock(TextMessage.class);
        ErrorData errorData = mock(ErrorData.class);

        when(pathMapper.processMessage(session, message)).thenReturn(Optional.of(errorData));

        subject.handleTextMessage(session, message);

        verify(pathMapper, times(1)).processMessage(eq(session), eq(message));
        verify(session, times(1)).sendMessage(any());
        verify(webSocketDeserializer, times(1)).mapAsWsResponse(any());
    }

    @Test
    public void shouldNotThrowException() throws IOException {
        WebSocketSession session = mock(WebSocketSession.class);
        TextMessage message = mock(TextMessage.class);
        ErrorData errorData = mock(ErrorData.class);

        when(pathMapper.processMessage(session, message)).thenReturn(Optional.of(errorData));
        when(webSocketDeserializer.mapAsWsResponse(any())).thenThrow(new RuntimeException());

        Assertions.assertDoesNotThrow(() -> subject.handleTextMessage(session, message));

        verify(pathMapper, times(1)).processMessage(eq(session), eq(message));
        verify(session, times(0)).sendMessage(any());
        verify(webSocketDeserializer, times(1)).mapAsWsResponse(any());
    }

}
