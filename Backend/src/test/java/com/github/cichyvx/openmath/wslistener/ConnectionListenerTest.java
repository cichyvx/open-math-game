package com.github.cichyvx.openmath.wslistener;

import com.github.cichyvx.openmath.model.ConnectionRequest;
import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.wsproducer.StatusChangeProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ConnectionListenerTest {

    private ConnectionListener subject;
    private SessionHandler sessionHandler;
    private StatusChangeProducer statusChangeProducer;

    @BeforeEach
    public void setUp() {
        sessionHandler = mock(SessionHandler.class);
        statusChangeProducer = mock(StatusChangeProducer.class);
        subject = new ConnectionListener(sessionHandler, statusChangeProducer);
    }

    @Test
    public void shouldReturnPath() {
        assertEquals("/connect", subject.path());
    }

    @Test
    public void shouldCallSessionHandler() {
        WebSocketSession stubSession = mock(WebSocketSession.class);
        ConnectionRequest stubMessage = mock(ConnectionRequest.class);

        subject.process(stubSession, stubMessage);

        verify(sessionHandler, times(1)).registerSession(eq(stubSession), eq(stubMessage));
    }

}
