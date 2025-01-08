package com.github.cichyvx.openmath.game;

import java.time.Instant;

public record MatchRoom(GameLoop gameLoop,
                        Instant endTime,
                        Score score,
                        String session1,
                        String session2) implements Comparable<MatchRoom> {

    @Override
    public int compareTo(MatchRoom o) {
        return this.endTime.compareTo(o.endTime);
    }
}
