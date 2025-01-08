package com.github.cichyvx.openmath.model;

import com.github.cichyvx.openmath.ws.UserState;

public record StatusChangeResponse(
        UserState state
) {
}
