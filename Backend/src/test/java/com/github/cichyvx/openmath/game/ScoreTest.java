package com.github.cichyvx.openmath.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScoreTest {

    @Test
    public void testScoreSession1() {
        String session1 = "player1";
        String session2 = "player2";
        Score score = new Score(session1, session2);

        score.score(session1);
        assertEquals(1, score.getScore(session1));
        assertEquals(0, score.getScore(session2));
    }

    @Test
    public void testScoreSession2() {
        String session1 = "player1";
        String session2 = "player2";
        Score score = new Score(session1, session2);

        score.score(session2);
        assertEquals(0, score.getScore(session1));
        assertEquals(1, score.getScore(session2));
    }

    @Test
    public void testGetScoreInvalidSession() {
        String session1 = "player1";
        String session2 = "player2";
        Score score = new Score(session1, session2);

        assertThrows(IllegalArgumentException.class, () -> score.getScore("invalid"));
    }

}
