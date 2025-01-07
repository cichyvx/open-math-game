package com.github.cichyvx.openmath.game;

import com.github.cichyvx.openmath.model.QuestionResponse;
import com.github.cichyvx.openmath.ws.WebSocketMessageSender;
import org.springframework.web.socket.WebSocketSession;

import java.util.Random;

public class Loop {

    private final WebSocketSession session1;
    private final WebSocketSession session2;
    private final WebSocketMessageSender webSocketMessageSender;
    private final static String[] operations = new String[] {"-", "+", "*", "/"};
    private final static Random random = new Random();

    private Equation equation;


    public Loop(WebSocketSession session1, WebSocketSession session2, WebSocketMessageSender webSocketMessageSender) {
        this.session1 = session1;
        this.session2 = session2;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    public void generateAndSendEquation() {
        this.equation = generateEquation();

        QuestionResponse question = new QuestionResponse(equation.equation());

        webSocketMessageSender.sendMessage(session1.getId(), question);
        webSocketMessageSender.sendMessage(session2.getId(), question);
    }

    public boolean answer(double answer) {
        return equation.answer() == answer;
    }

    private Equation generateEquation() {
        String operation = operations[random.nextInt(operations.length)];

        int firstDigit = random.nextInt(10);
        int secondDigit;

        do {
            secondDigit = random.nextInt(10);
        } while (operation.equals("/") && secondDigit == 0);

        String equation = firstDigit + " " + operation + " " + secondDigit;

        double result = switch (operation) {
            case "+" -> firstDigit + secondDigit;
            case "-" -> firstDigit - secondDigit;
            case "*" -> firstDigit * secondDigit;
            case "/" -> (double) firstDigit / secondDigit;
            default -> throw new IllegalArgumentException("Invalid operation: " + operation);
        };
        return new Equation(equation, result);
    }

}
