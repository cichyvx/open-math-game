package com.github.cichyvx.openmath.model;

import java.time.Instant;

public record GameInfoResponse(
        String enemyPlayerUsername,
        Instant gameStart
) {
}
