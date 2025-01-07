package com.github.cichyvx.openmath.game;

import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;

public record MatchRoom(Loop loop,
                        Instant endTime,
                        Score score,
                        WebSocketSession session1,
                        WebSocketSession session2) implements Comparable<MatchRoom> {

    @Override
    public int compareTo(MatchRoom o) {
        return this.endTime.compareTo(o.endTime);
    }
}
