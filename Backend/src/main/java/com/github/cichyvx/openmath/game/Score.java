package com.github.cichyvx.openmath.game;

public class Score {

    private final String session1;
    private final String session2;

    private int score1 = 0;
    private int score2 = 0;

    public Score(String session1, String session2) {
        this.session1 = session1;
        this.session2 = session2;
    }

    public void score(String session) {
        if (session.equals(session1)) {
            score1++;
        } else if (session.equals(session2)) {
            score2++;
        }
    }

    public int getScore(String session) {
        if (session.equals(session1)) {
            return score1;
        } else if (session.equals(session2)) {
            return score2;
        }
        throw new IllegalArgumentException("Invalid session");
    }
}
