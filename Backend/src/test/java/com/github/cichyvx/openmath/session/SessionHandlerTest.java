package com.github.cichyvx.openmath.session;

import com.github.cichyvx.openmath.exception.DeserializationException;
import com.github.cichyvx.openmath.exception.SessionAlreadyExistsException;
import com.github.cichyvx.openmath.model.request.ConnectionRequest;
import com.github.cichyvx.openmath.ws.SessionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Order needed because of static map 'sessions'
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SessionHandlerTest {

    private SessionHandler subject;

    @BeforeEach
    public void setUp() {
        subject = new SessionHandler();
    }

    @Test
    @Order(1)
    void shouldRegisterSessionSuccessfully() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        ConnectionRequest request = new ConnectionRequest("user1");

        subject.registerSession(session, request);
    }

    @Test
    @Order(2)
    void shouldThrowSessionAlreadyExistsWhenSessionIsDuplicate() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1"); //already created in test 1
        ConnectionRequest request = new ConnectionRequest("user1");

        assertThrows(SessionAlreadyExistsException.class, () -> subject.registerSession(session, request));
    }

    @Test
    @Order(3)
    void shouldThrowIllegalArgumentExceptionWhenSessionIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new SessionHandler.UserData(null, "user1", null));
    }

    @Test
    @Order(4)
    void shouldThrowDeserializationErrorWhenUsernameIsNull() {
        WebSocketSession session = mock(WebSocketSession.class);

        DeserializationException exception = assertThrows(
                DeserializationException.class, () -> new SessionHandler.UserData(session, null, null));
        assertEquals("username", exception.getMessage());
    }

    @Test
    @Order(5)
    void shouldThrowDeserializationErrorWhenUsernameIsEmpty() {
        WebSocketSession session = mock(WebSocketSession.class);

        DeserializationException exception = assertThrows(
                DeserializationException.class, () -> new SessionHandler.UserData(session, "", null));
        assertEquals("username", exception.getMessage());
    }
}
