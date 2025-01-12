package com.github.cichyvx.openmath.game;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class EquationGenerator {

    private final static String[] operations = new String[] {"-", "+", "*", "/"};
    private final static Random random = new Random();

    public Equation generateEquation() {
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
