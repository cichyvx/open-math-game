package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.exception.ExceptionHandler;
import com.github.cichyvx.openmath.model.response.ErrorData;
import com.github.cichyvx.openmath.model.request.GenericWsRequest;
import com.github.cichyvx.openmath.wslistener.WsListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WebSocketMessagePathMapperTest {

    private WebSocketMessagePathMapper subject;
    private WebSocketDeserializer webSocketDeserializer;
    private WsMessageValidator wsMessageValidator;
    private WebSocketListenerMapper webSocketListenerMapper;
    private ExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        webSocketDeserializer = mock(WebSocketDeserializer.class);
        wsMessageValidator = mock(WsMessageValidator.class);
        webSocketListenerMapper = mock(WebSocketListenerMapper.class);
        exceptionHandler = mock(ExceptionHandler.class);

        subject = new WebSocketMessagePathMapper(webSocketDeserializer, wsMessageValidator, webSocketListenerMapper, exceptionHandler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProcessMessage() {
        WebSocketSession sessionMock = mock(WebSocketSession.class);
        TextMessage textMessage = new TextMessage("hello");
        GenericWsRequest genericWsRequest = new GenericWsRequest("path", "data");
        WsListener<String> wsListenerMock = mock(WsListener.class);

        when(webSocketDeserializer.mapAsWsRequest(textMessage.asBytes())).thenReturn(genericWsRequest);
        when(webSocketListenerMapper.getListener(eq(genericWsRequest.path())))
                .thenReturn(Optional.of(new WebSocketListenerMapper.ListenerData(wsListenerMock, String.class)));
        when(webSocketDeserializer.convert(eq(genericWsRequest.data()), eq(String.class))).thenReturn("test");

        subject.processMessage(sessionMock, textMessage);

        verify(webSocketDeserializer, times(1)).mapAsWsRequest(eq(textMessage.asBytes()));
        verify(wsMessageValidator, times(1)).validate(eq(genericWsRequest));
        verify(webSocketListenerMapper, times(1)).getListener(eq(genericWsRequest.path()));
        verify(webSocketDeserializer, times(1)).convert(eq(genericWsRequest.data()), eq(String.class));
        verify(exceptionHandler, times(0)).handleException(any());
    }

    @Test
    public void shouldCallErrorHandlerOnExceptionThrow() {
        WebSocketSession sessionMock = mock(WebSocketSession.class);
        TextMessage textMessage = new TextMessage("hello");

        ErrorData expectedErrorData = new ErrorData(1, "test");
        RuntimeException expectedException = new RuntimeException();

        when(webSocketDeserializer.mapAsWsRequest(any())).thenThrow(expectedException);
        when(exceptionHandler.handleException(any())).thenReturn(expectedErrorData);

        var actual = subject.processMessage(sessionMock, textMessage);

        verify(webSocketDeserializer, times(1)).mapAsWsRequest(any());
        verify(wsMessageValidator, times(0)).validate(any());
        verify(webSocketListenerMapper, times(0)).getListener(any());
        verify(webSocketDeserializer, times(0)).convert(any(), any());
        verify(exceptionHandler, times(1)).handleException(eq(expectedException));

        assertTrue(actual.isPresent());
        assertEquals(expectedErrorData, actual.get());
    }
}
