package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.exception.AlreadyWaitingInMatchMakingException;
import com.github.cichyvx.openmath.exception.WrongUserStateException;
import com.github.cichyvx.openmath.model.response.StatusChangeResponse;
import com.github.cichyvx.openmath.ws.SessionHandler;
import com.github.cichyvx.openmath.ws.UserState;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerRegistrationTest {

    private PlayerRegistration playerRegistration;
    private SessionHandler sessionHandler;
    private WebSocketMessageSender webSocketMessageSender;
    private WebSocketSession webSocketSession;

    @BeforeEach
    void setUp() {
        sessionHandler = mock(SessionHandler.class);
        webSocketMessageSender = mock(WebSocketMessageSender.class);
        playerRegistration = new PlayerRegistration(sessionHandler, webSocketMessageSender);

        webSocketSession = mock(WebSocketSession.class);
    }

    @Test
    void testAddPlayerSuccessfully() {
        String sessionId = "session1";
        SessionHandler.UserData userData = new SessionHandler.UserData(webSocketSession, sessionId, UserState.CONNECTED);

        when(sessionHandler.getSession(sessionId)).thenReturn(Optional.of(userData));
        when(sessionHandler.changeUserState(sessionId, UserState.MATCHMAKING))
                .thenReturn(Optional.of(new SessionHandler.UserData(webSocketSession, sessionId, UserState.MATCHMAKING)));

        playerRegistration.add(sessionId);

        verify(webSocketMessageSender).sendMessage(eq(sessionId), any(StatusChangeResponse.class));
        assertEquals(1, PlayerRegistration.waitingSessionsSet.size());
    }

    @Test
    void testAddPlayerThrowsWrongUserStateException() {
        String sessionId = "session1";
        SessionHandler.UserData userData = new SessionHandler.UserData(webSocketSession, sessionId, UserState.IN_GAME);

        when(sessionHandler.getSession(sessionId)).thenReturn(Optional.of(userData));

        WrongUserStateException exception = assertThrows(WrongUserStateException.class,
                () -> playerRegistration.add(sessionId));

        assertTrue(exception.getMessage().contains("bad current user status"));
    }

    @Test
    void testAddPlayerThrowsAlreadyWaitingInMatchMakingException() {
        String sessionId = "session1";
        SessionHandler.UserData userData = new SessionHandler.UserData(webSocketSession, sessionId, UserState.CONNECTED);

        when(sessionHandler.getSession(sessionId)).thenReturn(Optional.of(userData));
        when(sessionHandler.changeUserState(sessionId, UserState.MATCHMAKING))
                .thenReturn(Optional.of(new SessionHandler.UserData(webSocketSession, sessionId, UserState.MATCHMAKING)));

        playerRegistration.add(sessionId);

        AlreadyWaitingInMatchMakingException exception = assertThrows(AlreadyWaitingInMatchMakingException.class,
                () -> playerRegistration.add(sessionId));

        assertNotNull(exception);
    }

    @Test
    void testGetPairOfLongestWaitingSessionId() {
        String session1 = "session1";
        String session2 = "session2";

        PlayerRegistration.WaitingSession ws1 = new PlayerRegistration.WaitingSession(session1, Instant.now().minusSeconds(10));
        PlayerRegistration.WaitingSession ws2 = new PlayerRegistration.WaitingSession(session2, Instant.now().minusSeconds(5));

        PlayerRegistration.waitingSessionIdsQueue.offer(ws1);
        PlayerRegistration.waitingSessionIdsQueue.offer(ws2);
        PlayerRegistration.waitingSessionsSet.add(ws1);
        PlayerRegistration.waitingSessionsSet.add(ws2);

        Optional<MatchingPlayersPair> pair = playerRegistration.getPairOfLongestWaitingSessionId();

        assertTrue(pair.isPresent());
        assertEquals(session1, pair.get().sessionId1());
        assertEquals(session2, pair.get().sessionId2());
    }

}
