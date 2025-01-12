package com.github.cichyvx.openmath.game;

import com.github.cichyvx.openmath.model.response.QuestionResponse;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;

public class GameLoop {

    private final String session1;
    private final String session2;
    private final WebSocketMessageSender webSocketMessageSender;
    private final EquationGenerator equationGenerator;

    private Equation equation;


    public GameLoop(String session1, String session2, WebSocketMessageSender webSocketMessageSender,
                    EquationGenerator equationGenerator) {
        this.session1 = session1;
        this.session2 = session2;
        this.webSocketMessageSender = webSocketMessageSender;
        this.equationGenerator = equationGenerator;
    }

    public void generateAndSendEquation() {
        this.equation = equationGenerator.generateEquation();

        QuestionResponse question = new QuestionResponse(equation.equation());

        webSocketMessageSender.sendMessage(session1, question);
        webSocketMessageSender.sendMessage(session2, question);
    }

    public boolean answer(double answer) {
        return equation.answer() == answer;
    }

}
