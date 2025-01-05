package com.github.cichyvx.openmath.model;

import jakarta.annotation.Nonnull;

public record GenericWsRequest(@Nonnull String path, Object data) {
}
