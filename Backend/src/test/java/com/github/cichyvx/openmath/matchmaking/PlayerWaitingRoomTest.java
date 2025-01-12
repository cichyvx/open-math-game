package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.config.OpenMathConfig;
import com.github.cichyvx.openmath.model.response.GameInfoResponse;
import com.github.cichyvx.openmath.ws.SessionHandler;
import com.github.cichyvx.openmath.ws.UserState;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class PlayerWaitingRoomTest {

    public static final long WAITING = 1L;
    private InGameRoomsHolder inGameRoomsHolder;
    private WebSocketMessageSender webSocketMessageSender;
    private SessionHandler sessionHandler;
    private PlayerWaitingRoom subject;
    private OpenMathConfig openMathConfig;
    private WebSocketSession webSocketSession;

    @BeforeEach
    public void setUp() {
        inGameRoomsHolder = mock(InGameRoomsHolder.class);
        webSocketMessageSender = mock(WebSocketMessageSender.class);
        sessionHandler = mock(SessionHandler.class);
        openMathConfig = mock(OpenMathConfig.class);
        subject = new PlayerWaitingRoom(inGameRoomsHolder, sessionHandler, webSocketMessageSender, openMathConfig);

        webSocketSession = mock(WebSocketSession.class);
    }

    @Test
    @Disabled
    public void shouldRemoveFromQueueAfterTime() throws InterruptedException {
        String session1 = "1";
        String session2 = "2";

        when(sessionHandler.getSession(eq(session1))).thenReturn(Optional.of(mock(SessionHandler.UserData.class)));
        when(sessionHandler.getSession(eq(session2))).thenReturn(Optional.of(mock(SessionHandler.UserData.class)));
        when(openMathConfig.getWaitingRoomTime()).thenReturn(WAITING);

        subject.add(session1, session2);

        subject.addAsStarted();
        verify(inGameRoomsHolder, times(0)).add(any());

        TimeUnit.SECONDS.sleep(WAITING + 5L);

        subject.addAsStarted();
        verify(inGameRoomsHolder, times(1)).add(any());
    }

    @Test
    void testAddSuccessfully() {
        // Mocking inputs
        String session1 = "session1";
        String session2 = "session2";
        long waitingTime = 30L;

        SessionHandler.UserData userData1 = new SessionHandler.UserData(webSocketSession, session1, UserState.WAITING_FOR_GAME_START);
        SessionHandler.UserData userData2 = new SessionHandler.UserData(webSocketSession, session2, UserState.WAITING_FOR_GAME_START);

        when(sessionHandler.getSession(session1)).thenReturn(java.util.Optional.of(userData1));
        when(sessionHandler.getSession(session2)).thenReturn(java.util.Optional.of(userData2));
        when(openMathConfig.getWaitingRoomTime()).thenReturn(waitingTime);

        // Execution
        subject.add(session1, session2);

        // Verify game information messages
        verify(webSocketMessageSender).sendMessage(eq(session1), any(GameInfoResponse.class));
        verify(webSocketMessageSender).sendMessage(eq(session2), any(GameInfoResponse.class));
    }

    @Test
    void testAddThrowsExceptionWhenSession1NotFound() {
        String session1 = "session1";
        String session2 = "session2";

        when(sessionHandler.getSession(session1)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> subject.add(session1, session2));

        verify(sessionHandler).getSession(session1);
        verifyNoInteractions(webSocketMessageSender);
    }

    @Test
    void testAddThrowsExceptionWhenSession2NotFound() {
        String session1 = "session1";
        String session2 = "session2";

        SessionHandler.UserData userData1 = new SessionHandler.UserData(webSocketSession, session1, UserState.WAITING_FOR_GAME_START);

        when(sessionHandler.getSession(session1)).thenReturn(java.util.Optional.of(userData1));
        when(sessionHandler.getSession(session2)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> subject.add(session1, session2));

        verify(sessionHandler).getSession(session1);
        verify(sessionHandler).getSession(session2);
        verifyNoInteractions(webSocketMessageSender);
    }

}
