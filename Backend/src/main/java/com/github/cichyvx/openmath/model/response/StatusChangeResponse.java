package com.github.cichyvx.openmath.model.response;

import com.github.cichyvx.openmath.ws.UserState;

public record StatusChangeResponse(
        UserState state
) {
}
