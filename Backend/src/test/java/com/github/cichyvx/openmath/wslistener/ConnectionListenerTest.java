package com.github.cichyvx.openmath.wslistener;

import com.github.cichyvx.openmath.model.ConnectionRequest;
import com.github.cichyvx.openmath.model.StatusChangeResponse;
import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.session.UserState;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConnectionListenerTest {

    private ConnectionListener subject;
    private SessionHandler sessionHandler;
    private WebSocketMessageSender webSocketMessageSender;

    @BeforeEach
    public void setUp() {
        sessionHandler = mock(SessionHandler.class);
        webSocketMessageSender = mock(WebSocketMessageSender.class);
        subject = new ConnectionListener(sessionHandler, webSocketMessageSender);
    }

    @Test
    public void shouldReturnPath() {
        assertEquals("/connect", subject.path());
    }

    @Test
    public void shouldCallSessionHandlerAndProducer() {
        WebSocketSession stubSession = mock(WebSocketSession.class);
        ConnectionRequest stubMessage = mock(ConnectionRequest.class);

        when(sessionHandler.registerSession(eq(stubSession), eq(stubMessage)))
                .thenReturn(new SessionHandler.UserData(stubSession, "user", UserState.CONNECTED));

        subject.process(stubSession, stubMessage);

        verify(sessionHandler, times(1)).registerSession(eq(stubSession), eq(stubMessage));
        verify(webSocketMessageSender, times(1)).sendMessage(eq(stubSession.getId()), eq(new StatusChangeResponse(UserState.CONNECTED)));
    }

}
