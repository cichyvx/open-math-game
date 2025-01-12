package com.github.cichyvx.openmath.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EquationGeneratorTest {
    private final EquationGenerator subject = new EquationGenerator();

    @Test
    public void testGenerateEquation() {
        var opertaionList = List.of("-", "+", "*", "/");
        for (int i = 0; i < 1000; i++) {
            Equation equation = subject.generateEquation();

            var equationTab = equation.equation().split(" ");

            double firstDigit = Integer.parseInt(equationTab[0]);
            String operation = equationTab[1];
            double secondDigit = Integer.parseInt(equationTab[2]);

            assertEquals(3, equationTab.length);
            assertTrue(firstDigit >= -10 && firstDigit <= 10);
            assertTrue(secondDigit >= -10 && secondDigit <= 10);
            assertTrue(opertaionList.contains(operation));

            switch (operation) {
                case "+" -> assertEquals(firstDigit + secondDigit, equation.answer());
                case "*" -> assertEquals(firstDigit * secondDigit, equation.answer());
                case "/" -> assertEquals(firstDigit / secondDigit, equation.answer());
                case "-" -> assertEquals(firstDigit - secondDigit, equation.answer());
                default -> fail();
            }
        }
    }
}
