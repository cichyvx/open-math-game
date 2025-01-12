package com.github.cichyvx.openmath.game;

import com.github.cichyvx.openmath.model.response.QuestionResponse;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameLoopTest {

    private GameLoop subject;
    private WebSocketMessageSender webSocketMessageSender;
    private EquationGenerator equationGenerator;
    private final String session1 = "session1";
    private final String session2 = "session2";

    @BeforeEach
    public void setUp() {
        webSocketMessageSender = mock(WebSocketMessageSender.class);
        equationGenerator = mock(EquationGenerator.class);
        subject = new GameLoop(session1, session2, webSocketMessageSender, equationGenerator);
    }

    @Test
    public void shouldGenerateAndSendEquation() {
        var expectedQuestion = new QuestionResponse("equation");
        Equation equation = mock(Equation.class);
        when(equationGenerator.generateEquation()).thenReturn(equation);
        when(equation.equation()).thenReturn(expectedQuestion.equation());


        subject.generateAndSendEquation();

        verify(webSocketMessageSender, times(1)).sendMessage(eq(session1), eq(expectedQuestion));
        verify(webSocketMessageSender, times(1)).sendMessage(eq(session2), eq(expectedQuestion));
        verify(webSocketMessageSender, times(2)).sendMessage(anyString(), eq(expectedQuestion));
    }

    @ParameterizedTest
    @ValueSource(doubles = {1, 2})
    public void answerTest(double answer) {
        Equation equation = mock(Equation.class);
        when(equationGenerator.generateEquation()).thenReturn(equation);
        subject.generateAndSendEquation();

        double correctAnswer = 2D;
        when(equation.answer()).thenReturn(correctAnswer);

        assertEquals(answer == correctAnswer, subject.answer(answer));

    }

}
