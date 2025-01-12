package com.github.cichyvx.openmath.ws;

import com.github.cichyvx.openmath.exception.SessionAlreadyExistsException;
import com.github.cichyvx.openmath.model.request.ConnectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Order needed because of static map 'sessions'
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SessionHandlerTest {

    public static final String SESSION_ID_REMOVE_CASE = "id";
    public static final WebSocketSession WEB_SOCKET_SESSION = mock(WebSocketSession.class);
    public static final ConnectionRequest REQUEST = mock(ConnectionRequest.class);
    private SessionHandler subject;

    @BeforeEach
    public void setUp() {
        subject = new SessionHandler();
        when(WEB_SOCKET_SESSION.getId()).thenReturn(SESSION_ID_REMOVE_CASE);
        when(REQUEST.username()).thenReturn("username");
    }

    @Test
    @Order(1)
    void shouldCreatePlainSessionSuccessfully() {
        subject.createPlaintSession(WEB_SOCKET_SESSION);

        Optional<SessionHandler.UserData> result = subject.getSession(SESSION_ID_REMOVE_CASE);

        assertTrue(result.isPresent());

        assertEquals(WEB_SOCKET_SESSION, result.get().session());
        assertEquals(UserState.CONNECTED, result.get().state());
    }

    @Test
    @Order(2)
    public void shouldRegiserSessionSuccessfully() {
        var result = subject.registerSession(SESSION_ID_REMOVE_CASE, REQUEST);

        assertEquals(WEB_SOCKET_SESSION, result.session());
        assertEquals(REQUEST.username(), result.username());
        assertEquals(UserState.CONNECTED, result.state());
    }

    @Test
    @Order(3)
    public void shouldThrowWhenSessionExistPlainSession() {
        assertThrows(SessionAlreadyExistsException.class, () -> subject.createPlaintSession(WEB_SOCKET_SESSION));
    }

    @Test
    @Order(4)
    public void shouldChangeStatus() {
        var result = subject.changeUserState(SESSION_ID_REMOVE_CASE, UserState.MATCHMAKING);

        assertTrue(result.isPresent());
        assertEquals(WEB_SOCKET_SESSION, result.get().session());
        assertEquals(REQUEST.username(), result.get().username());
        assertEquals(UserState.MATCHMAKING, result.get().state());
    }

    @Test
    @Order(5)
    public void shouldThrowWhenSessionNotConnected() {
        assertThrows(SessionAlreadyExistsException.class, () -> subject.registerSession(SESSION_ID_REMOVE_CASE, REQUEST));
    }

    @Test
    @Order(6)
    void shouldRemoveSessionSuccessfully() {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn(SESSION_ID_REMOVE_CASE);

        subject.removeSession(session);

        assertTrue(subject.getSession(SESSION_ID_REMOVE_CASE).isEmpty());
    }
}
