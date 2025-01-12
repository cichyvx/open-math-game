package com.github.cichyvx.openmath.matchmaking;

import com.github.cichyvx.openmath.game.Equation;
import com.github.cichyvx.openmath.game.EquationGenerator;
import com.github.cichyvx.openmath.game.GameLoop;
import com.github.cichyvx.openmath.game.MatchRoom;
import com.github.cichyvx.openmath.game.Score;
import com.github.cichyvx.openmath.model.request.AnswerRequest;
import com.github.cichyvx.openmath.model.response.AnswerResponse;
import com.github.cichyvx.openmath.model.response.StatusChangeResponse;
import com.github.cichyvx.openmath.ws.SessionHandler;
import com.github.cichyvx.openmath.ws.UserState;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InGameRoomsHolderTest {

    private InGameRoomsHolder subject;
    private SessionHandler sessionHandler;
    private WebSocketMessageSender webSocketMessageSender;
    private EquationGenerator equationGenerator;

    private GameLoop mockGameLoop;

    @BeforeEach
    public void setUp() {
        sessionHandler = mock(SessionHandler.class);
        webSocketMessageSender = mock(WebSocketMessageSender.class);
        equationGenerator = mock(EquationGenerator.class);

        subject = new InGameRoomsHolder(sessionHandler, webSocketMessageSender, equationGenerator);
    }

    @Test
    void testAddRoom() {
        Room room = mock(Room.class);
        String session1 = "session1";
        String session2 = "session2";

        WebSocketSession webSocketSession1 = mock(WebSocketSession.class);

        when(room.session1()).thenReturn(session1);
        when(room.session2()).thenReturn(session2);
        when(sessionHandler.changeUserState(eq(session1), eq(UserState.IN_GAME))).thenReturn(Optional.of(new SessionHandler.UserData(webSocketSession1, session1, UserState.IN_GAME)));
        when(sessionHandler.changeUserState(eq(session2), eq(UserState.IN_GAME))).thenReturn(Optional.of(new SessionHandler.UserData(webSocketSession1, session2, UserState.IN_GAME)));
        when(equationGenerator.generateEquation()).thenReturn(new Equation("1 + 1", 2));

        subject.add(room);

        verify(webSocketMessageSender).sendMessage(eq(session1), any(StatusChangeResponse.class));
        verify(webSocketMessageSender).sendMessage(eq(session2), any(StatusChangeResponse.class));
    }

    @Test
    void testAnswerCorrect() {
        String sessionId = "session1";
        MatchRoom matchRoom = mock(MatchRoom.class);
        GameLoop gameLoop = mock(GameLoop.class);
        Score score = mock(Score.class);

        when(matchRoom.gameLoop()).thenReturn(gameLoop);
        when(matchRoom.score()).thenReturn(score);
        when(matchRoom.session1()).thenReturn(sessionId);
        when(matchRoom.session2()).thenReturn("session2");
        when(score.getScore(anyString())).thenReturn(10);
        when(gameLoop.answer(anyDouble())).thenReturn(true);

        AnswerRequest answerRequest = new AnswerRequest(5);
        InGameRoomsHolder.matchRoomsMap.put(sessionId, matchRoom);

        subject.answer(sessionId, answerRequest);

        verify(gameLoop).generateAndSendEquation();
        verify(webSocketMessageSender).sendMessage(eq(sessionId), any(AnswerResponse.class));
    }

    @Test
    void testAnswerIncorrect() {
        String sessionId = "session1";
        MatchRoom matchRoom = mock(MatchRoom.class);
        GameLoop gameLoop = mock(GameLoop.class);

        when(matchRoom.gameLoop()).thenReturn(gameLoop);
        when(gameLoop.answer(anyInt())).thenReturn(false);

        AnswerRequest answerRequest = new AnswerRequest(5);
        InGameRoomsHolder.matchRoomsMap.put(sessionId, matchRoom);

        subject.answer(sessionId, answerRequest);

        verify(gameLoop, never()).generateAndSendEquation();
        verify(webSocketMessageSender, never()).sendMessage(anyString(), any(AnswerResponse.class));
    }
}
