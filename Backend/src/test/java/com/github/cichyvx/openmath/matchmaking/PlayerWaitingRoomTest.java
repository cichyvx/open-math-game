package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.session.SessionHandler;
import com.github.cichyvx.openmath.wsproducer.GameStartInfoProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerWaitingRoomTest {

    private InGameRoomsHolder inGameRoomsHolder;
    private GameStartInfoProducer gameStartInfoProducer;
    private SessionHandler sessionHandler;
    private PlayerWaitingRoom subject;

    @BeforeEach
    public void setUp() {
        inGameRoomsHolder = mock(InGameRoomsHolder.class);
        gameStartInfoProducer = mock(GameStartInfoProducer.class);
        sessionHandler = mock(SessionHandler.class);
        subject = new PlayerWaitingRoom(inGameRoomsHolder, gameStartInfoProducer, sessionHandler);
    }

    @Test
    public void shouldRemoveFromQueueAfterTime() throws InterruptedException {
        String session1 = "1";
        String session2 = "2";

        when(sessionHandler.getSession(anyString())).thenReturn(Optional.of(mock(SessionHandler.UserData.class)));

        subject.add(session1, session2);

        subject.addAsStarted();
        verify(inGameRoomsHolder, times(0)).add(any());

        TimeUnit.SECONDS.sleep(PlayerWaitingRoom.SECONDS_TO_WAIT);

        subject.addAsStarted();
        verify(inGameRoomsHolder, times(1)).add(any());
    }

}
