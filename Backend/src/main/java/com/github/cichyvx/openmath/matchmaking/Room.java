package com.github.cichyvx.openmath.matchmaking;

import java.time.Instant;

public record Room(String session1, String session2, Instant eventTime) implements Comparable<Room> {
    @Override
    public int compareTo(Room o) {
        return eventTime.compareTo(o.eventTime);
    }
}
