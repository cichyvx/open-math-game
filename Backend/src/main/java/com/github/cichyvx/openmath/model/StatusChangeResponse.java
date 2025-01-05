package com.github.cichyvx.openmath.model;

import com.github.cichyvx.openmath.session.UserState;

public record StatusChangeResponse(
        UserState state
) {
}
