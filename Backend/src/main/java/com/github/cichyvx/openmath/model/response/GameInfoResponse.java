package com.github.cichyvx.openmath.model.response;

import java.time.Instant;

public record GameInfoResponse(
        String enemyPlayerUsername,
        Instant gameStart
) {
}
